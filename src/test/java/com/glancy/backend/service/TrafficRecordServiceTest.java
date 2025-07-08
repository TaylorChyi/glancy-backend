package com.glancy.backend.service;

import com.glancy.backend.dto.TrafficRecordRequest;
import com.glancy.backend.dto.TrafficRecordResponse;
import com.glancy.backend.repository.TrafficRecordRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrafficRecordServiceTest {

    @Autowired
    private TrafficRecordService trafficRecordService;
    @Autowired
    private TrafficRecordRepository trafficRecordRepository;

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
        trafficRecordRepository.deleteAll();
    }

    @Test
    void recordAndCount() {
        TrafficRecordRequest req = new TrafficRecordRequest();
        req.setPath("/");
        TrafficRecordResponse resp = trafficRecordService.record(req);
        assertNotNull(resp.getId());

        long count = trafficRecordService.count(LocalDate.now(), LocalDate.now());
        assertEquals(1, count);

        List<Long> daily = trafficRecordService.countDaily(LocalDate.now(), LocalDate.now());
        assertEquals(1, daily.size());
        assertEquals(1L, daily.get(0));
    }
}
