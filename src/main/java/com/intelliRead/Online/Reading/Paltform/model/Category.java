package com.intelliRead.Online.Reading.Paltform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "category")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String categoryName;

    private String description;

    // ✅ FIXED: Unique JSON reference for parent category
    @JsonBackReference("category-parent")
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    // ✅ FIXED: Matching JSON reference for subcategories
    @JsonManagedReference("category-parent")
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private List<Category> subCategories = new ArrayList<>();

    // ✅ FIXED: Books relationship with proper JSON reference
    @JsonManagedReference("category-books")
    @OneToMany(mappedBy = "category")
    private List<Book> books = new ArrayList<>();
}