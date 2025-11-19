package com.intelliRead.Online.Reading.Paltform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "download_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime downloadedAt;

    @Column(nullable = false)
    private String downloadType = "FULL"; // FULL, SAMPLE

    @Column
    private String ipAddress;

    @Column
    private String userAgent;
}