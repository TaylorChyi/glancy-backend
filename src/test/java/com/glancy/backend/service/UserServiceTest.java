package com.glancy.backend.service;

import com.glancy.backend.dto.UserRegistrationRequest;
import com.glancy.backend.dto.UserResponse;
import com.glancy.backend.dto.LoginRequest;
import com.glancy.backend.entity.User;
import com.glancy.backend.entity.LoginDevice;
import com.glancy.backend.repository.UserRepository;
import com.glancy.backend.repository.LoginDeviceRepository;

import io.github.cdimascio.dotenv.Dotenv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        userService.register(req1);

        // 再次用相同用户名
        UserRegistrationRequest req2 = new UserRegistrationRequest();
        req2.setUsername("user1");
        req2.setPassword("pass456");
        req2.setEmail("b@example.com");

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
        userService.register(req1);

        // 再次用相同邮箱
        UserRegistrationRequest req2 = new UserRegistrationRequest();
        req2.setUsername("user2");
        req2.setPassword("pass456");
        req2.setEmail("a@example.com");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(req2);
        });
        assertEquals("邮箱已被使用", ex.getMessage());
    }

    @Test
    void testLoginDeviceLimit() {
        // create user
        UserRegistrationRequest req = new UserRegistrationRequest();
        req.setUsername("deviceuser");
        req.setPassword("pass123");
        req.setEmail("device@example.com");
        UserResponse resp = userService.register(req);

        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("deviceuser");
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
        assertFalse(devices.stream().anyMatch(d -> "d1".equals(d.getDeviceInfo())));    }

    @Test
    void testEnableMembership() {
        UserRegistrationRequest req = new UserRegistrationRequest();
        req.setUsername("memberuser");
        req.setPassword("pass123");
        req.setEmail("member@example.com");
        UserResponse resp = userService.register(req);

        UserResponse updated = userService.enableMembership(resp.getId());
        assertTrue(userRepository.findById(resp.getId()).orElseThrow().getMember());
        assertEquals(resp.getId(), updated.getId());
    }
}