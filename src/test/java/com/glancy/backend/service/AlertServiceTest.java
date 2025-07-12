package com.glancy.backend.service;

import com.glancy.backend.entity.AlertRecipient;
import com.glancy.backend.entity.SystemParameter;
import com.glancy.backend.repository.AlertRecipientRepository;
import com.glancy.backend.repository.SystemParameterRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class AlertServiceTest {

    @Autowired
    private AlertService alertService;
    @Autowired
    private AlertRecipientRepository alertRecipientRepository;
    @Autowired
    private SystemParameterRepository parameterRepository;

    @MockBean
    private JavaMailSender mailSender;

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
        parameterRepository.deleteAll();
    }

    @Test
    void sendAlertDisabled() {
        AlertRecipient r = new AlertRecipient();
        r.setEmail("a@example.com");
        alertRecipientRepository.save(r);

        alertService.sendAlert("s", "b");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendAlertEnabled() {
        AlertRecipient r = new AlertRecipient();
        r.setEmail("a@example.com");
        alertRecipientRepository.save(r);

        SystemParameter p = new SystemParameter();
        p.setName("email.notifications.enabled");
        p.setValue("true");
        parameterRepository.save(p);

        alertService.sendAlert("s", "b");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
