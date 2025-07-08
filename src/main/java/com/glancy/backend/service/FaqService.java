package com.glancy.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.glancy.backend.dto.FaqRequest;
import com.glancy.backend.dto.FaqResponse;
import com.glancy.backend.entity.Faq;
import com.glancy.backend.repository.FaqRepository;

/**
 * Business logic for FAQ management. Allows admins to create and
 * retrieve frequently asked questions.
 */
@Slf4j
@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    /**
     * Persist a new FAQ entry.
     */
    @Transactional
    public FaqResponse createFaq(FaqRequest request) {
        log.info("Creating FAQ: {}", request.getQuestion());
        Faq faq = new Faq();
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        Faq saved = faqRepository.save(faq);
        return toResponse(saved);
    }

    /**
     * Retrieve all stored FAQ entries.
     */
    @Transactional(readOnly = true)
    public List<FaqResponse> getAllFaqs() {
        log.info("Retrieving all FAQs");
        return faqRepository.findAll().stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    private FaqResponse toResponse(Faq faq) {
        return new FaqResponse(faq.getId(), faq.getQuestion(), faq.getAnswer());
    }
}
