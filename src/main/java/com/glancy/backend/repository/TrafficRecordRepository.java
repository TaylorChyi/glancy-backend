package com.glancy.backend.repository;

import com.glancy.backend.entity.TrafficRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repository for portal traffic records.
 */
@Repository
public interface TrafficRecordRepository extends JpaRepository<TrafficRecord, Long> {
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
