package com.glancy.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.glancy.backend.repository.SystemParameterRepository;
import com.glancy.backend.entity.SystemParameter;

import java.util.List;
import java.util.stream.Collectors;

import com.glancy.backend.entity.AlertRecipient;
import com.glancy.backend.repository.AlertRecipientRepository;

/**
 * Sends alert emails when errors occur.
 */
@Slf4j
@Service
public class AlertService {

    private final JavaMailSender mailSender;
    private final AlertRecipientRepository recipientRepository;
    private final SystemParameterRepository parameterRepository;

    public AlertService(JavaMailSender mailSender,
                        AlertRecipientRepository recipientRepository,
                        SystemParameterRepository parameterRepository) {
        this.mailSender = mailSender;
        this.recipientRepository = recipientRepository;
        this.parameterRepository = parameterRepository;
    }

    /**
     * Send an alert email with the provided subject and body.
     */
    public void sendAlert(String subject, String body) {
        boolean enabled = parameterRepository.findByName("email.notifications.enabled")
                .map(SystemParameter::getValue)
                .map(Boolean::parseBoolean)
                .orElse(false);
        if (!enabled) {
            return;
        }
        List<String> recipients = recipientRepository.findAll().stream()
                .map(AlertRecipient::getEmail)
                .filter(e -> e != null && !e.isBlank())
                .collect(Collectors.toList());
        if (recipients.isEmpty()) {
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(recipients.toArray(new String[0]));
        msg.setSubject(subject);
        msg.setText(body);
        try {
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Failed to send alert email", e);
        }
    }
}
