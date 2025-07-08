package com.glancy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Indicates whether a user has purchased membership.
 */
@Data
@AllArgsConstructor
public class MemberStatusResponse {
    private Long userId;
    private Boolean member;
}
