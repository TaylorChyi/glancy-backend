package com.glancy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Common fields shared by multiple entities.
 */
@MappedSuperclass
@Data
@NoArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
