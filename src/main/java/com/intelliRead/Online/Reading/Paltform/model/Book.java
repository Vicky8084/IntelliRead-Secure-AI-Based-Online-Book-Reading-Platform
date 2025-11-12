package com.intelliRead.Online.Reading.Paltform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.intelliRead.Online.Reading.Paltform.enums.BookStatus;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    // Category relationship - EAGER banao
    @JsonIgnoreProperties({"books", "subCategories"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    // User relationship - EAGER banao
    @JsonBackReference("user-books")
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    // Book has reviews - TINO ANNOTATIONS USE KARO
    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private List<Review> reviewList;

    /* --- New fields for file handling --- */

    // original filename
    @Column
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status = BookStatus.PENDING;

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