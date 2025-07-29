package com.glancy.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import com.glancy.backend.config.OssProperties;

/**
 * Simple tests for OssAvatarStorage.
 */
class OssAvatarStorageTest {

    /**
     * 测试 uploadWithoutClientThrows 接口
     */
    @Test
    void uploadWithoutClientThrows() {
        OssProperties props = new OssProperties();
        props.setEndpoint("https://oss-cn-beijing.aliyuncs.com");
        props.setBucket("bucket");
        props.setAccessKeyId("");
        props.setAccessKeySecret("");
        props.setAvatarDir("avatars/");
        OssAvatarStorage storage = new OssAvatarStorage(props);
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "data".getBytes());
        assertThrows(IllegalStateException.class, () -> storage.upload(file));
    }
}
