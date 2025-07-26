package com.glancy.backend.service;

import com.glancy.backend.dto.AlertRecipientRequest;
import com.glancy.backend.dto.AlertRecipientResponse;
import com.glancy.backend.entity.AlertRecipient;
import com.glancy.backend.repository.AlertRecipientRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AlertRecipientServiceTest {

    @Autowired
    private AlertRecipientService alertRecipientService;
    @Autowired
    private AlertRecipientRepository alertRecipientRepository;

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
        alertRecipientRepository.deleteAll();
    }

    /**
     * 测试 testAddAndUpdateRecipient 接口
     */
    @Test
    void testAddAndUpdateRecipient() {
        AlertRecipientRequest req = new AlertRecipientRequest();
        req.setEmail("a@example.com");
        AlertRecipientResponse resp = alertRecipientService.addRecipient(req);
        assertNotNull(resp.getId());
        assertEquals("a@example.com", resp.getEmail());

        AlertRecipientRequest updateReq = new AlertRecipientRequest();
        updateReq.setEmail("b@example.com");
        AlertRecipientResponse updated = alertRecipientService.updateRecipient(resp.getId(), updateReq);
        assertEquals("b@example.com", updated.getEmail());

        AlertRecipient entity = alertRecipientRepository.findById(resp.getId()).orElseThrow();
        assertEquals("b@example.com", entity.getEmail());
    }
}
