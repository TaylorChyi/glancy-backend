package com.glancy.backend.service;

import com.glancy.backend.dto.TrafficRecordRequest;
import com.glancy.backend.dto.TrafficRecordResponse;
import com.glancy.backend.entity.TrafficRecord;
import com.glancy.backend.repository.TrafficRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods to log and retrieve portal traffic information.
 */
@Service
public class TrafficRecordService {
    private final TrafficRecordRepository trafficRecordRepository;

    public TrafficRecordService(TrafficRecordRepository trafficRecordRepository) {
        this.trafficRecordRepository = trafficRecordRepository;
    }

    /**
     * Save a traffic record when the portal is visited.
     */
    @Transactional
    public TrafficRecordResponse record(TrafficRecordRequest request) {
        TrafficRecord record = new TrafficRecord();
        record.setPath(request.getPath());
        record.setIp(request.getIp());
        record.setUserAgent(request.getUserAgent());
        TrafficRecord saved = trafficRecordRepository.save(record);
        return toResponse(saved);
    }

    /**
     * Count visits between the provided dates (inclusive).
     */
    @Transactional(readOnly = true)
    public long count(LocalDate start, LocalDate end) {
        LocalDateTime s = start.atStartOfDay();
        LocalDateTime e = end.plusDays(1).atStartOfDay();
        return trafficRecordRepository.countByCreatedAtBetween(s, e);
    }

    /**
     * Retrieve daily counts for a date range.
     */
    @Transactional(readOnly = true)
    public List<Long> countDaily(LocalDate start, LocalDate end) {
        List<Long> result = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            long c = count(cursor, cursor);
            result.add(c);
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    private TrafficRecordResponse toResponse(TrafficRecord record) {
        return new TrafficRecordResponse(record.getId(), record.getPath(),
                record.getIp(), record.getUserAgent(), record.getCreatedAt());
    }
}
