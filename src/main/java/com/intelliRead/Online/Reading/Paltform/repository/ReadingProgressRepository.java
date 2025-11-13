package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.model.ReadingProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Integer> {

    Optional<ReadingProgress> findByUserIdAndBookId(int userId, int bookId);

    List<ReadingProgress> findByUserId(int userId);

    @Query("SELECT rp FROM ReadingProgress rp WHERE rp.user.id = :userId ORDER BY rp.lastRead DESC")
    List<ReadingProgress> findRecentProgressByUserId(@Param("userId") int userId);

    @Query("SELECT rp FROM ReadingProgress rp WHERE rp.user.id = :userId AND rp.progressPercentage >= 80 ORDER BY rp.lastRead DESC")
    List<ReadingProgress> findNearlyCompletedBooks(@Param("userId") int userId);
}