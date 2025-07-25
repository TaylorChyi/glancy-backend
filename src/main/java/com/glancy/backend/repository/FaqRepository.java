package com.glancy.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glancy.backend.entity.Faq;

/**
 * Repository for FAQ entities allowing CRUD operations.
 */
@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
}
