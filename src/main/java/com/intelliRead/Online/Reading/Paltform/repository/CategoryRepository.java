package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // ✅ Find by category name
    Optional<Category> findByCategoryName(String categoryName);

    // ✅ Find main categories (no parent)
    List<Category> findByParentCategoryIsNull();

    // ✅ Find subcategories by parent
    List<Category> findByParentCategoryId(int parentId);

    // ✅ Find categories with most books (for recommendations)
    @Query("SELECT c FROM Category c ORDER BY SIZE(c.books) DESC")
    List<Category> findPopularCategories();
}