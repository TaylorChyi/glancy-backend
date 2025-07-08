package com.glancy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Records each device a user has logged in from.
 */
@Entity
@Table(name = "login_devices")
@Data
@NoArgsConstructor
public class LoginDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String deviceInfo;

    @Column(nullable = false)
    private LocalDateTime loginTime = LocalDateTime.now();
}
