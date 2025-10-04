package com.intelliRead.Online.Reading.Paltform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "AI_Summaries")
public class AISummaries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String chapter; //Chapter/Title

    @Column(nullable = false)
    private String summary; // AI generated Summary

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
