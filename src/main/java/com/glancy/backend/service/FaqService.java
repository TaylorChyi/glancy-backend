package com.glancy.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glancy.backend.dto.FaqRequest;
import com.glancy.backend.dto.FaqResponse;
import com.glancy.backend.entity.Faq;
import com.glancy.backend.repository.FaqRepository;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Transactional
    public FaqResponse createFaq(FaqRequest request) {
        Faq faq = new Faq();
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        Faq saved = faqRepository.save(faq);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FaqResponse> getAllFaqs() {
        return faqRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private FaqResponse toResponse(Faq faq) {
        return new FaqResponse(faq.getId(), faq.getQuestion(), faq.getAnswer());
    }
}
