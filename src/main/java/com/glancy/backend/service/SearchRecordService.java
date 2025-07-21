package com.glancy.backend.service;

import com.glancy.backend.dto.SearchRecordRequest;
import com.glancy.backend.dto.SearchRecordResponse;
import com.glancy.backend.entity.SearchRecord;
import com.glancy.backend.entity.User;
import com.glancy.backend.repository.SearchRecordRepository;
import com.glancy.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Manages persistence of search records and enforces daily limits
 * for non-member users.
 */
@Slf4j
@Service
public class SearchRecordService {
    private final SearchRecordRepository searchRecordRepository;
    private final UserRepository userRepository;
    private final int nonMemberSearchLimit;

    public SearchRecordService(SearchRecordRepository searchRecordRepository,
                               UserRepository userRepository,
                               @Value("${search.limit.nonMember:10}") int nonMemberSearchLimit) {
        this.searchRecordRepository = searchRecordRepository;
        this.userRepository = userRepository;
        this.nonMemberSearchLimit = nonMemberSearchLimit;
    }

    /**
     * Save a search record for a user and apply daily limits if the
     * user is not a member.
     */
    @Transactional
    public SearchRecordResponse saveRecord(Long userId, SearchRecordRequest request) {
        log.info("Saving search record for user {} with term '{}'", userId, request.getTerm());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found", userId);
                    return new IllegalArgumentException("用户不存在");
                });
        if (user.getLastLoginAt() == null) {
            log.warn("User {} is not logged in", userId);
            throw new IllegalStateException("用户未登录");
        }
        SearchRecord existing = searchRecordRepository
                .findTopByUserIdAndTermAndLanguageOrderByCreatedAtDesc(userId,
                        request.getTerm(), request.getLanguage());
        if (existing != null) {
            return toResponse(existing);
        }

        if (Boolean.FALSE.equals(user.getMember())) {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            long count = searchRecordRepository
                    .countByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);
            if (count >= nonMemberSearchLimit) {
                log.warn("User {} exceeded daily search limit", userId);
                throw new IllegalStateException("非会员每天只能搜索" + nonMemberSearchLimit + "次");
            }
        }
        SearchRecord record = new SearchRecord();
        record.setUser(user);
        record.setTerm(request.getTerm());
        record.setLanguage(request.getLanguage());
        SearchRecord saved = searchRecordRepository.save(record);
        return toResponse(saved);
    }

    /**
     * Mark a search record as favorite for the user.
     */
    @Transactional
    public SearchRecordResponse favoriteRecord(Long userId, Long recordId) {
        log.info("Favoriting search record {} for user {}", recordId, userId);
        SearchRecord record = searchRecordRepository.findByIdAndUserId(recordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("搜索记录不存在"));
        record.setFavorite(true);
        SearchRecord saved = searchRecordRepository.save(record);
        return toResponse(saved);
    }

    /**
     * Retrieve a user's search history ordered by creation time.
     */
    @Transactional(readOnly = true)
    public List<SearchRecordResponse> getRecords(Long userId) {
        log.info("Fetching search records for user {}", userId);
        return searchRecordRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Remove all search records for the given user.
     */
    @Transactional
    public void clearRecords(Long userId) {
        log.info("Clearing search records for user {}", userId);
        searchRecordRepository.deleteByUserId(userId);
    }

    /**
     * Cancel favorite status for a user's search record.
     */
    @Transactional
    public void unfavoriteRecord(Long userId, Long recordId) {
        log.info("Unfavoriting search record {} for user {}", recordId, userId);
        SearchRecord record = searchRecordRepository.findByIdAndUserId(recordId, userId)
                .orElseThrow(() -> new IllegalArgumentException("记录不存在"));
        record.setFavorite(false);
        searchRecordRepository.save(record);
    }
  
    /**
     * Delete a single search record belonging to the given user.
     */
    @Transactional
    public void deleteRecord(Long userId, Long recordId) {
        log.info("Deleting search record {} for user {}", recordId, userId);
        SearchRecord record = searchRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("搜索记录不存在"));
        if (!record.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("搜索记录不存在");
        }
        searchRecordRepository.delete(record);
    }

    private SearchRecordResponse toResponse(SearchRecord record) {
        return new SearchRecordResponse(record.getId(), record.getUser().getId(),
                record.getTerm(), record.getLanguage(), record.getCreatedAt(), record.getFavorite());
    }
}
