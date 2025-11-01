package com.intelliRead.Online.Reading.Paltform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.intelliRead.Online.Reading.Paltform.enums.SuggestionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "suggestion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Suggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String suggestedTitle;

    private String author; // Optional

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuggestionStatus suggestionStatus = SuggestionStatus.PENDING;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Unique back reference for user
    @JsonBackReference("user-suggestions")
    @ManyToOne
    private User user;
}
