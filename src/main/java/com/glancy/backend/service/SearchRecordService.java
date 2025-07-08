package com.glancy.backend.service;

import com.glancy.backend.dto.SearchRecordRequest;
import com.glancy.backend.dto.SearchRecordResponse;
import com.glancy.backend.entity.SearchRecord;
import com.glancy.backend.entity.User;
import com.glancy.backend.repository.SearchRecordRepository;
import com.glancy.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchRecordService {
    private final SearchRecordRepository searchRecordRepository;
    private final UserRepository userRepository;

    public SearchRecordService(SearchRecordRepository searchRecordRepository,
                               UserRepository userRepository) {
        this.searchRecordRepository = searchRecordRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SearchRecordResponse saveRecord(Long userId, SearchRecordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        SearchRecord record = new SearchRecord();
        record.setUser(user);
        record.setTerm(request.getTerm());
        record.setLanguage(request.getLanguage());
        SearchRecord saved = searchRecordRepository.save(record);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SearchRecordResponse> getRecords(Long userId) {
        return searchRecordRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void clearRecords(Long userId) {
        searchRecordRepository.deleteByUserId(userId);
    }

    private SearchRecordResponse toResponse(SearchRecord record) {
        return new SearchRecordResponse(record.getId(), record.getUser().getId(),
                record.getTerm(), record.getLanguage(), record.getCreatedAt());
    }
}
