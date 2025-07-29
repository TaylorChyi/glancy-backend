package com.glancy.backend.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.glancy.backend.config.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;


import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import java.util.Date;

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
    private final String securityToken;
    private final String avatarDir;
    private final boolean publicRead;
    private final long signedUrlExpirationMinutes;
    private final boolean verifyLocation;
    private String urlPrefix;

    private OSS ossClient;

    public OssAvatarStorage(OssProperties properties) {
        this.endpoint = properties.getEndpoint();
        this.bucket = properties.getBucket();
        this.accessKeyId = properties.getAccessKeyId();
        this.accessKeySecret = properties.getAccessKeySecret();
        this.avatarDir = properties.getAvatarDir();
        this.publicRead = properties.isPublicRead();
        this.verifyLocation = properties.isVerifyLocation();
        this.signedUrlExpirationMinutes = properties.getSignedUrlExpirationMinutes();
        this.securityToken = properties.getSecurityToken();
        this.urlPrefix = String.format("https://%s.%s/", bucket, removeProtocol(endpoint));
    }

    @PostConstruct
    public void init() {
        if (accessKeyId != null && !accessKeyId.isEmpty()
                && accessKeySecret != null && !accessKeySecret.isEmpty()) {
            if (securityToken != null && !securityToken.isEmpty()) {
                this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId,
                        accessKeySecret, securityToken);
            } else {
                this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            }
            if (verifyLocation) {
                try {
                    String location = ossClient.getBucketLocation(bucket);
                    String expected = formatEndpoint(location);
                    String configured = removeProtocol(endpoint);
                    if (!configured.contains(location)) {
                        ossClient.shutdown();
                        if (securityToken != null && !securityToken.isEmpty()) {
                            this.ossClient = new OSSClientBuilder().build(expected,
                                    accessKeyId, accessKeySecret, securityToken);
                        } else {
                            this.ossClient = new OSSClientBuilder().build(expected,
                                    accessKeyId, accessKeySecret);
                        }
                        this.endpoint = expected;
                        this.urlPrefix = String.format("https://%s.%s/", bucket, removeProtocol(expected));
                    }
                } catch (Exception e) {
                    // log and continue with configured endpoint
                    log.warn("Failed to verify OSS endpoint: {}", e.getMessage());
                }
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
        boolean isPublic = setPublicReadAcl(objectName);
        if (isPublic) {
            return urlPrefix + objectName;
        }
        return generatePresignedUrl(objectName);
    }

    /**
     * Attempt to mark the uploaded object as publicly readable.
     * Some buckets do not allow changing object ACLs. If an access error occurs
     * we simply log a warning and continue using the bucket's default ACL.
     */
    private boolean setPublicReadAcl(String objectName) {
        if (!publicRead) {
            return false;
        }
        try {
            ossClient.setObjectAcl(bucket, objectName, CannedAccessControlList.PublicRead);
            return true;
        } catch (OSSException | ClientException e) {
            log.warn("Unable to set public ACL for {}: {}", objectName, e.getMessage());
            return false;
        }
    }

    private String generatePresignedUrl(String objectName) {
        Date expiration = new Date(System.currentTimeMillis()
                + Duration.ofMinutes(signedUrlExpirationMinutes).toMillis());
        URL url;
        if (securityToken != null && !securityToken.isEmpty()) {
            GeneratePresignedUrlRequest req =
                    new GeneratePresignedUrlRequest(bucket, objectName, HttpMethod.GET);
            req.setExpiration(expiration);
            req.setSecurityToken(securityToken);
            url = ossClient.generatePresignedUrl(req);
        } else {
            url = ossClient.generatePresignedUrl(bucket, objectName, expiration);
        }
        return url.toString();
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
