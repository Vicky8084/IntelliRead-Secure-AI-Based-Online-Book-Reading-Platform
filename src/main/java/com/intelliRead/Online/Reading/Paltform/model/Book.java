package com.intelliRead.Online.Reading.Paltform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "book")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    // keep name exactly as you had it
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime uploadedAt;

    // ✅ ADDED: Category relationship
    @JsonBackReference("category-books")
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Unique back reference name for user
    @JsonBackReference("user-books")
    @ManyToOne
    private User user;

    // Book has reviews
    @JsonManagedReference("book-reviews")
    @OneToMany(mappedBy = "book")
    private List<Review> reviewList;

    /* --- New fields for file handling --- */

    // original filename
    @Column
    private String fileName;

    // physical path (relative) to stored file (pdf or text)
    @Column
    private String filePath;

    // cover image path (relative)
    @Column
    private String coverImagePath;

    // file type, e.g. "pdf", "txt"
    @Column
    private String fileType;

    // file size in bytes
    @Column
    private Long fileSize;

    // extracted text (optional - can be null)
    @Column(columnDefinition = "LONGTEXT")
    private String extractedText;
}