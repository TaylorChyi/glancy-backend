package com.glancy.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
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

    /**
     * 测试 setPublicReadAcl failure is swallowed
     */
    @Test
    void aclDeniedDoesNotThrow() throws Exception {
        OssProperties props = new OssProperties();
        props.setEndpoint("https://oss-cn-beijing.aliyuncs.com");
        props.setBucket("bucket");
        props.setAccessKeyId("id");
        props.setAccessKeySecret("secret");
        props.setAvatarDir("avatars/");
        props.setPublicRead(true);

        OssAvatarStorage storage = new OssAvatarStorage(props);

        OSS client = mock(OSS.class);
        when(client.putObject(eq("bucket"), anyString(), any())).thenReturn(null);
        OSSException ex = new OSSException("AccessDenied");
        ex.setErrorCode("AccessDenied");
        doThrow(ex).when(client).setObjectAcl(eq("bucket"), anyString(), eq(CannedAccessControlList.PublicRead));

        var field = OssAvatarStorage.class.getDeclaredField("ossClient");
        field.setAccessible(true);
        field.set(storage, client);

        MockMultipartFile file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "data".getBytes());
        storage.upload(file);
        verify(client).setObjectAcl(eq("bucket"), anyString(), eq(CannedAccessControlList.PublicRead));
    }
}
