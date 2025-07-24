package com.glancy.backend.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Strategy interface for storing avatar images.
 */
public interface AvatarStorage {
    /**
     * Upload the avatar file and return the accessible URL.
     */
    String upload(MultipartFile file) throws IOException;
}
