package com.glancy.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Simple tests for OssAvatarStorage.
 */
class OssAvatarStorageTest {

    @Test
    void uploadWithoutClientThrows() {
        OssAvatarStorage storage = new OssAvatarStorage(
                "https://oss-cn-hangzhou.aliyuncs.com",
                "bucket", "", "", "avatars/");
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "data".getBytes());
        assertThrows(IllegalStateException.class, () -> storage.upload(file));
    }
}
