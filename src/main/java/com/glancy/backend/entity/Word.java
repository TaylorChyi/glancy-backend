package com.glancy.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dictionary word entry cached from the external service.
 */
@Entity
@Table(name = "words",
       uniqueConstraints = @UniqueConstraint(columnNames = {"term", "language"}))
@Data
@NoArgsConstructor
public class Word extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String term;

    @ElementCollection
    @CollectionTable(name = "word_definitions", joinColumns = @JoinColumn(name = "word_id"))
    @Column(name = "definition", nullable = false, columnDefinition = "TEXT")
    private List<String> definitions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Language language;

    @Column(length = 100)
    private String phonetic;

    @Column
    private String example;
}
