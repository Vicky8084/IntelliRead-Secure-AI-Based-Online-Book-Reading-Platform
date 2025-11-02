package com.intelliRead.Online.Reading.Paltform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.intelliRead.Online.Reading.Paltform.enums.Role;
import com.intelliRead.Online.Reading.Paltform.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore // ✅ Password frontend ko nahi bhejenge
    private String passwordHash;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;  // PUBLISHER / USER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // ACTIVE / INACTIVE

    @Column(nullable = false)
    private String preferredLanguage;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    // Unique reference names for each relationship
    @JsonManagedReference("user-books")
    @OneToMany(mappedBy = "user")
    private List<Book> bookList;

    @JsonManagedReference("user-reviews")
    @OneToMany(mappedBy = "user")
    private List<Review> reviewList;

    @JsonManagedReference("user-suggestions")
    @OneToMany(mappedBy = "user")
    private List<Suggestion> suggestionList;
}