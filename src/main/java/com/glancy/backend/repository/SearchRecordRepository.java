package com.glancy.backend.repository;

import com.glancy.backend.entity.SearchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;
import com.glancy.backend.entity.Language;

/**
 * Repository for persisting and querying user search history.
 */
@Repository
public interface SearchRecordRepository extends JpaRepository<SearchRecord, Long> {
    List<SearchRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByUserId(Long userId);
    long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    boolean existsByUserIdAndTermAndLanguage(Long userId, String term, Language language);
    SearchRecord findTopByUserIdAndTermAndLanguageOrderByCreatedAtDesc(Long userId, String term, Language language);
}
