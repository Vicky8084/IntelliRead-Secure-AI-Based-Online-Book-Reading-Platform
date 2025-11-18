package com.intelliRead.Online.Reading.Paltform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "suggestion_votes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSuggestionVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonBackReference("user-votes")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonBackReference("suggestion-votes")
    @ManyToOne
    @JoinColumn(name = "suggestion_id", nullable = false)
    private Suggestion suggestion;

    @Column(nullable = false)
    private boolean upvoted = true;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime votedAt;
}