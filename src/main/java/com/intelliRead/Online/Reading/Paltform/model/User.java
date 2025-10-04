package com.intelliRead.Online.Reading.Paltform.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.intelliRead.Online.Reading.Paltform.enums.Role;
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

    @Column(nullable = false,unique = true)
    private  String email;

    @Column(nullable = false)
    private  String passwordHash;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String preferredLanguage;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Book> bookList;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Review> reviewList;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Suggestion> suggestionList;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Notification> notificationList;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<SearchHistory> searchHistoryList;
}
