package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // ✅ Check if user already reviewed this book
    Optional<Review> findByUserIdAndBookId(int userId, int bookId);

    // ✅ Find reviews by book ID
    List<Review> findByBookId(int bookId);

    // ✅ Find reviews by user ID
    List<Review> findByUserId(int userId);

    // ✅ NEW: Delete all reviews for a book
    @Modifying
    @Query("DELETE FROM Review r WHERE r.book.id = :bookId")
    void deleteByBookId(@Param("bookId") int bookId);

    // ✅ NEW: Count reviews for a book
    int countByBookId(int bookId);
}