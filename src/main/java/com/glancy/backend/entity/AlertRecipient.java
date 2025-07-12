package com.glancy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stores an email address that should receive alert notifications.
 */
@Entity
@Table(name = "alert_recipients")
@Data
@NoArgsConstructor
public class AlertRecipient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;
}
