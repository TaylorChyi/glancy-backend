package com.glancy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a runtime configurable parameter that can be adjusted
 * via the portal endpoints.
 */
@Entity
@Table(name = "system_parameters")
@Data
@NoArgsConstructor
public class SystemParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, length = 255)
    private String value;
}
