package com.glancy.backend.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import com.glancy.backend.config.OssProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.UUID;

/**
 * Handles uploading avatar images to Alibaba Cloud OSS.
 */
@Service
public class OssAvatarStorage implements AvatarStorage {
    private final String endpoint;
    private final String bucket;
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String avatarDir;
    private final String urlPrefix;

    private OSS ossClient;

    public OssAvatarStorage(OssProperties properties) {
        this.endpoint = properties.getEndpoint();
        this.bucket = properties.getBucket();
        this.accessKeyId = properties.getAccessKeyId();
        this.accessKeySecret = properties.getAccessKeySecret();
        this.avatarDir = properties.getAvatarDir();
        this.urlPrefix = String.format("https://%s.%s/", bucket, endpoint.replaceFirst("https?://", ""));
    }

    @PostConstruct
    public void init() {
        if (accessKeyId != null && !accessKeyId.isEmpty()
                && accessKeySecret != null && !accessKeySecret.isEmpty()) {
            this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
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
}
