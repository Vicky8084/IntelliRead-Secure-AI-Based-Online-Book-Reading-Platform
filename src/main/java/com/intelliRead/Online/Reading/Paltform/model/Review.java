package com.intelliRead.Online.Reading.Paltform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int rating; // Integer rating (1-5)

    @Column(nullable = false)
    private String reviewText; // Review Text Content

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Unique back reference for user
    @JsonBackReference("user-reviews")
    @ManyToOne
    private User user;

    // Unique back reference for book
    @JsonBackReference("book-reviews")
    @ManyToOne
    private Book book;
}
