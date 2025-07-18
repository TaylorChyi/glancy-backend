package com.glancy.backend.service;

import com.glancy.backend.dto.NotificationRequest;
import com.glancy.backend.dto.NotificationResponse;
import com.glancy.backend.entity.User;
import com.glancy.backend.repository.NotificationRepository;
import com.glancy.backend.repository.UserRepository;

import io.github.cdimascio.dotenv.Dotenv;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String dbPassword = dotenv.get("DB_PASSWORD");
        if (dbPassword != null) {
            System.setProperty("DB_PASSWORD", dbPassword);
        }
    }

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateAndQueryNotifications() {
        User user = new User();
        user.setUsername("u1");
        user.setPassword("pass");
        user.setEmail("u1@example.com");
        user.setPhone("11");
        userRepository.save(user);

        NotificationRequest req = new NotificationRequest();
        req.setMessage("sys msg");
        NotificationResponse sys = notificationService.createSystemNotification(req);
        assertTrue(sys.getSystemLevel());

        NotificationRequest ureq = new NotificationRequest();
        ureq.setMessage("user msg");
        NotificationResponse uresp = notificationService.createUserNotification(user.getId(), ureq);
        assertFalse(uresp.getSystemLevel());
        assertEquals(user.getId(), uresp.getUserId());

        List<NotificationResponse> list = notificationService.getNotificationsForUser(user.getId());
        assertEquals(2, list.size());
        assertEquals("user msg", list.get(0).getMessage());
        assertEquals("sys msg", list.get(1).getMessage());
    }
}
