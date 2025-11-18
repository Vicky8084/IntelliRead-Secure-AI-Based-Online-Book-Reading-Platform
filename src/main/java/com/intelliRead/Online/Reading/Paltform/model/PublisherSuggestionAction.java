package com.intelliRead.Online.Reading.Paltform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.intelliRead.Online.Reading.Paltform.enums.PublisherAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "publisher_suggestion_actions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublisherSuggestionAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublisherAction action;

    @Column(columnDefinition = "TEXT")
    private String publisherNotes;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships
    @JsonBackReference("publisher-actions")
    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private User publisher;

    @JsonBackReference("suggestion-actions")
    @ManyToOne
    @JoinColumn(name = "suggestion_id", nullable = false)
    private Suggestion suggestion;

    // Reference to the book that was uploaded (if any)
    private Integer uploadedBookId;
}