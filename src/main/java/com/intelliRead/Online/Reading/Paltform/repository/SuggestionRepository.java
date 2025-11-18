package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.enums.SuggestionStatus;
import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Integer> {
    @Query("SELECT s FROM Suggestion s WHERE s.user.id = :userId")
    List<Suggestion> findByUserId(@Param("userId") int userId);

    // Find by status
    List<Suggestion> findBySuggestionStatus(SuggestionStatus suggestionStatus);

    // Count by status for admin dashboard
    long countBySuggestionStatus(SuggestionStatus suggestionStatus);
    // Add these methods to your existing SuggestionRepository
    @Query("SELECT s FROM Suggestion s WHERE s.suggestionStatus = 'PENDING' ORDER BY s.createdAt DESC")
    List<Suggestion> findPendingSuggestionsOrderByDate();

    @Query("SELECT s FROM Suggestion s WHERE s.user.id = :userId AND s.suggestionStatus = :status")
    List<Suggestion> findByUserIdAndSuggestionStatus(@Param("userId") int userId,
                                                     @Param("status") SuggestionStatus status);

    @Query("SELECT COUNT(s) FROM Suggestion s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Suggestion s WHERE s.suggestionStatus = 'APPROVED' AND s.createdAt >= :date")
    List<Suggestion> findApprovedSuggestionsAfterDate(@Param("date") LocalDateTime date);
}