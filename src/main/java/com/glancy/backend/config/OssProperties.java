package com.glancy.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oss")
public class OssProperties {
    private String endpoint;
    private String bucket;
    private String accessKeyId;
    private String accessKeySecret;
    private String avatarDir = "avatars/";
    /**
     * Whether uploaded objects should be publicly readable.
     */
    private boolean publicRead = true;

    /**
     * Expiration time in minutes for generated presigned URLs when objects are
     * not public.
     */
    private long signedUrlExpirationMinutes = 1440;
}
