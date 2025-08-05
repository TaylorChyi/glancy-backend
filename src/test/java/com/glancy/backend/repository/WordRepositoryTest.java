package com.glancy.backend.repository;

import com.glancy.backend.entity.Language;
import com.glancy.backend.entity.Word;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WordRepositoryTest {

    @Autowired
    private WordRepository wordRepository;

    @Test
    void findByTermAndLanguageAndDeletedFalse() {
        Word word = TestEntityFactory.word("hello", Language.EN);
        wordRepository.save(word);

        Optional<Word> found = wordRepository.findByTermAndLanguageAndDeletedFalse("hello", Language.EN);
        assertTrue(found.isPresent());
        assertEquals("hello", found.get().getTerm());
    }
}
