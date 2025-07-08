package com.glancy.backend.repository;

import com.glancy.backend.entity.SearchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRecordRepository extends JpaRepository<SearchRecord, Long> {
    List<SearchRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByUserId(Long userId);
}
