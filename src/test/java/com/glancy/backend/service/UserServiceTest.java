package com.glancy.backend.service;

import com.glancy.backend.dto.UserRegistrationRequest;
import com.glancy.backend.dto.UserResponse;
import com.glancy.backend.dto.LoginRequest;
import com.glancy.backend.dto.LoginIdentifier;
import com.glancy.backend.dto.AvatarResponse;
import com.glancy.backend.entity.User;
import com.glancy.backend.entity.LoginDevice;
import com.glancy.backend.repository.UserRepository;
import com.glancy.backend.repository.LoginDeviceRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import io.github.cdimascio.dotenv.Dotenv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginDeviceRepository loginDeviceRepository;
    @MockitoBean
    private AvatarStorageService avatarStorageService;

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()  // 如果没有 .env 文件也不报错
                .load();

        String dbPassword = dotenv.get("DB_PASSWORD");
        if (dbPassword != null) {
            System.setProperty("DB_PASSWORD", dbPassword);
        }
    }

    @BeforeEach
    void setUp() {
        loginDeviceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testRegisterAndDeleteUser() {
        System.out.println("========================DB_PASSWORD is null. Please check your .env file.");

        // 注册
        UserRegistrationRequest req = new UserRegistrationRequest();
        req.setUsername("testuser");
        req.setPassword("pass123");
        req.setEmail("test@example.com");
        req.setPhone("100");
        UserResponse resp = userService.register(req);

        assertNotNull(resp.getId());
        assertEquals("testuser", resp.getUsername());

        // 验证数据库中未删除
        User user = userRepository.findById(resp.getId()).orElseThrow();
        assertFalse(user.getDeleted());

        // 注销
        userService.deleteUser(resp.getId());
        User deletedUser = userRepository.findById(resp.getId()).orElseThrow();
        assertTrue(deletedUser.getDeleted());
    }

    @Test
    void testRegisterDuplicateUsername() {
        // 准备一条用户
        UserRegistrationRequest req1 = new UserRegistrationRequest();
        req1.setUsername("user1");
        req1.setPassword("pass123");
        req1.setEmail("a@example.com");
        req1.setPhone("101");
        userService.register(req1);

        // 再次用相同用户名
        UserRegistrationRequest req2 = new UserRegistrationRequest();
        req2.setUsername("user1");
        req2.setPassword("pass456");
        req2.setEmail("b@example.com");
        req2.setPhone("102");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(req2);
        });
        assertEquals("用户名已存在", ex.getMessage());
    }

    @Test
    void testRegisterDuplicateEmail() {
        // 准备一条用户
        UserRegistrationRequest req1 = new UserRegistrationRequest();
        req1.setUsername("user1");
        req1.setPassword("pass123");
        req1.setEmail("a@example.com");
        req1.setPhone("111");
        userService.register(req1);

        // 再次用相同邮箱
        UserRegistrationRequest req2 = new UserRegistrationRequest();
        req2.setUsername("user2");
        req2.setPassword("pass456");
        req2.setEmail("a@example.com");
        req2.setPhone("112");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(req2);
        });
        assertEquals("邮箱已被使用", ex.getMessage());
    }

    @Test
    void testRegisterDuplicatePhone() {
        UserRegistrationRequest req1 = new UserRegistrationRequest();
        req1.setUsername("userp1");
        req1.setPassword("pass123");
        req1.setEmail("p1@example.com");
        req1.setPhone("12345");
        userService.register(req1);

        UserRegistrationRequest req2 = new UserRegistrationRequest();
        req2.setUsername("userp2");
        req2.setPassword("pass456");
        req2.setEmail("p2@example.com");
        req2.setPhone("12345");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(req2);
        });
        assertEquals("手机号已被使用", ex.getMessage());
    }

    @Test
    void testLoginDeviceLimit() {
        // create user
        UserRegistrationRequest req = new UserRegistrationRequest();
        req.setUsername("deviceuser");
        req.setPassword("pass123");
        req.setEmail("device@example.com");
        req.setPhone("103");
        UserResponse resp = userService.register(req);

        LoginIdentifier id = new LoginIdentifier();
        id.setType(LoginIdentifier.Type.USERNAME);
        id.setText("deviceuser");
        LoginRequest loginReq = new LoginRequest();
        loginReq.setIdentifier(id);
        loginReq.setPassword("pass123");

        loginReq.setDeviceInfo("d1");
        userService.login(loginReq);
        loginReq.setDeviceInfo("d2");
        userService.login(loginReq);
        loginReq.setDeviceInfo("d3");
        userService.login(loginReq);
        loginReq.setDeviceInfo("d4");
        userService.login(loginReq);

        List<LoginDevice> devices = loginDeviceRepository
                .findByUserIdOrderByLoginTimeAsc(resp.getId());
        assertEquals(3, devices.size());
        assertFalse(devices.stream().anyMatch(d -> "d1".equals(d.getDeviceInfo())));
    }

    @Test
    void testLoginByPhone() {
        UserRegistrationRequest req = new UserRegistrationRequest();
        req.setUsername("phoneuser");
        req.setPassword("pass123");
        req.setEmail("phone@example.com");
        req.setPhone("555");
        userService.register(req);

        LoginIdentifier id = new LoginIdentifier();
        id.setType(LoginIdentifier.Type.PHONE);
        id.setText("555");
        LoginRequest loginReq = new LoginRequest();
        loginReq.setIdentifier(id);
        loginReq.setPassword("pass123");

        assertNotNull(userService.login(loginReq).getToken());
    }

    @Test
    void testUpdateAvatar() {
        UserRegistrationRequest req = new UserRegistrationRequest();
        req.setUsername("avataruser");
        req.setPassword("pass123");
        req.setEmail("avatar@example.com");
        req.setPhone("104");
        UserResponse resp = userService.register(req);

        AvatarResponse updated = userService.updateAvatar(resp.getId(), "url");
        assertEquals("url", updated.getAvatar());

        AvatarResponse fetched = userService.getAvatar(resp.getId());
        assertEquals("url", fetched.getAvatar());
    }

    @Test
    void testUploadAvatar() throws Exception {
        UserRegistrationRequest req = new UserRegistrationRequest();
        req.setUsername("uploaduser");
        req.setPassword("pass");
        req.setEmail("up@example.com");
        req.setPhone("109");
        UserResponse resp = userService.register(req);

        MultipartFile file = mock(MultipartFile.class);
        when(avatarStorageService.upload(file)).thenReturn("path/url.jpg");

        AvatarResponse result = userService.uploadAvatar(resp.getId(), file);
        assertEquals("path/url.jpg", result.getAvatar());
    }

    @Test
    void testCountActiveUsers() {
        User u1 = new User();
        u1.setUsername("a1");
        u1.setPassword("p");
        u1.setEmail("a1@example.com");
        u1.setPhone("201");
        userRepository.save(u1);

        User u2 = new User();
        u2.setUsername("a2");
        u2.setPassword("p");
        u2.setEmail("a2@example.com");
        u2.setPhone("202");
        u2.setDeleted(true);
        userRepository.save(u2);

        long count = userService.countActiveUsers();
        assertEquals(1, count);
    }

    @Test
    void testMembershipOps() {
        UserRegistrationRequest req = new UserRegistrationRequest();
        req.setUsername("member");
        req.setPassword("p");
        req.setEmail("m@example.com");
        req.setPhone("203");
        UserResponse resp = userService.register(req);

        userService.activateMembership(resp.getId());
        User user = userRepository.findById(resp.getId()).orElseThrow();
        assertTrue(user.getMember());

        userService.removeMembership(resp.getId());
        User user2 = userRepository.findById(resp.getId()).orElseThrow();
        assertFalse(user2.getMember());
    }
}