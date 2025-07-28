package com.glancy.backend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Core user entity storing login credentials and profile info.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // Optional avatar image URL
    private String avatar;

    // Phone number
    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(nullable = false)
    private Boolean member = false;

    private LocalDateTime lastLoginAt;

    @JsonIgnore
    private String loginToken;
}
