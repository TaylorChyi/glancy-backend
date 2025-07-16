package com.glancy.backend.service;

import com.glancy.backend.dto.SearchRecordRequest;
import com.glancy.backend.dto.SearchRecordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.entity.User;
import com.glancy.backend.repository.SearchRecordRepository;
import com.glancy.backend.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "search.limit.nonMember=2")
@Transactional
class SearchRecordServiceTest {

    @Autowired
    private SearchRecordService searchRecordService;
    @Autowired
    private SearchRecordRepository searchRecordRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String dbPassword = dotenv.get("DB_PASSWORD");
        if (dbPassword != null) {
            System.setProperty("DB_PASSWORD", dbPassword);
        }
    }

    @BeforeEach
    void setUp() {
        searchRecordRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testSaveListAndClear() {
        User user = new User();
        user.setUsername("sruser");
        user.setPassword("p");
        user.setEmail("s@example.com");
        userRepository.save(user);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        SearchRecordRequest req = new SearchRecordRequest();
        req.setTerm("hello");
        req.setLanguage(Language.ENGLISH);
        SearchRecordResponse saved = searchRecordService.saveRecord(user.getId(), req);
        assertNotNull(saved.getId());

        List<SearchRecordResponse> list = searchRecordService.getRecords(user.getId());
        assertEquals(1, list.size());
        assertEquals("hello", list.get(0).getTerm());

        searchRecordService.clearRecords(user.getId());
        assertTrue(searchRecordService.getRecords(user.getId()).isEmpty());
    }

    @Test
    void testSaveRecordWithoutLogin() {
        User user = new User();
        user.setUsername("nologin");
        user.setPassword("p");
        user.setEmail("n@example.com");
        userRepository.save(user);

        SearchRecordRequest req = new SearchRecordRequest();
        req.setTerm("hi");
        req.setLanguage(Language.ENGLISH);

        Exception ex = assertThrows(IllegalStateException.class,
                () -> searchRecordService.saveRecord(user.getId(), req));
        assertEquals("用户未登录", ex.getMessage());
    }

    @Test
    void testNonMemberLimitExceeded() {
        User user = new User();
        user.setUsername("limit");
        user.setPassword("p");
        user.setEmail("l@example.com");
        userRepository.save(user);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        SearchRecordRequest req = new SearchRecordRequest();
        req.setTerm("hi");
        req.setLanguage(Language.ENGLISH);

        searchRecordService.saveRecord(user.getId(), req);
        searchRecordService.saveRecord(user.getId(), req);
        Exception ex = assertThrows(IllegalStateException.class,
                () -> searchRecordService.saveRecord(user.getId(), req));
        assertEquals("非会员每天只能搜索2次", ex.getMessage());
    }
}
