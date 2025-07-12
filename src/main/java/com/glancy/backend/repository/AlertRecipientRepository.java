package com.glancy.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glancy.backend.entity.AlertRecipient;

/**
 * Repository managing {@link AlertRecipient} records.
 */
@Repository
public interface AlertRecipientRepository extends JpaRepository<AlertRecipient, Long> {
    boolean existsByEmail(String email);
}
