package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    Optional<Book> findBookByTitle(String titleName);

    // ✅ CORRECTED: Use proper field names from Book entity
    Optional<Book> findByTitleAndUser_Id(String title, int userId);

    // ✅ CORRECTED: Use proper field name
    List<Book> findByUser_Id(int userId);

    // ✅ ADDED: Category related methods
    List<Book> findByCategoryId(int categoryId);
    List<Book> findByCategoryCategoryName(String categoryName);
}