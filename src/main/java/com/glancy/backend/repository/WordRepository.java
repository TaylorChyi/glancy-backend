package com.glancy.backend.repository;

import com.glancy.backend.entity.Language;
import com.glancy.backend.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    Optional<Word> findByTermAndLanguageAndDeletedFalse(String term, Language language);
}
