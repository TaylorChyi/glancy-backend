package com.glancy.backend.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import com.glancy.backend.config.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.UUID;

/**
 * Handles uploading avatar images to Alibaba Cloud OSS.
 */
@Service
@Slf4j
public class OssAvatarStorage implements AvatarStorage {
    private String endpoint;
    private final String bucket;
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String avatarDir;
    private String urlPrefix;

    private OSS ossClient;

    public OssAvatarStorage(OssProperties properties) {
        this.endpoint = properties.getEndpoint();
        this.bucket = properties.getBucket();
        this.accessKeyId = properties.getAccessKeyId();
        this.accessKeySecret = properties.getAccessKeySecret();
        this.avatarDir = properties.getAvatarDir();
        this.urlPrefix = String.format("https://%s.%s/", bucket, removeProtocol(endpoint));
    }

    @PostConstruct
    public void init() {
        if (accessKeyId != null && !accessKeyId.isEmpty()
                && accessKeySecret != null && !accessKeySecret.isEmpty()) {
            this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            try {
                String location = ossClient.getBucketLocation(bucket);
                String expected = formatEndpoint(location);
                String configured = removeProtocol(endpoint);
                if (!configured.contains(location)) {
                    ossClient.shutdown();
                    this.ossClient = new OSSClientBuilder().build(expected, accessKeyId, accessKeySecret);
                    this.endpoint = expected;
                    this.urlPrefix = String.format("https://%s.%s/", bucket, removeProtocol(expected));
                }
            } catch (Exception e) {
                // log and continue with configured endpoint
                log.warn("Failed to verify OSS endpoint: {}", e.getMessage());
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    /**
     * Upload the avatar and return the public URL.
     */
    public String upload(MultipartFile file) throws IOException {
        if (ossClient == null) {
            throw new IllegalStateException("OSS client not configured");
        }
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String objectName = avatarDir + UUID.randomUUID() + ext;
        ossClient.putObject(bucket, objectName, file.getInputStream());
        return urlPrefix + objectName;
    }

    private static String removeProtocol(String url) {
        return url.replaceFirst("https?://", "");
    }

    private static String formatEndpoint(String location) {
        String loc = location.startsWith("http") ? removeProtocol(location) : location;
        if (!loc.startsWith("oss-")) {
            loc = "oss-" + loc;
        }
        return "https://" + loc + ".aliyuncs.com";
    }
}
