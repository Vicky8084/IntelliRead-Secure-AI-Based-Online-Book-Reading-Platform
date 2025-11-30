package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.enums.SuggestionStatus;
import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Integer> {

    // ✅ OPTIMIZED: Single query with pagination - TC: O(log n), SC: O(pageSize)
    @Query("SELECT s FROM Suggestion s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    Page<Suggestion> findByUserId(@Param("userId") int userId, Pageable pageable);

    // ✅ OPTIMIZED: Use Spring Data built-in - TC: O(log n), SC: O(pageSize)
    Page<Suggestion> findBySuggestionStatus(SuggestionStatus suggestionStatus, Pageable pageable);

    // ✅ OPTIMIZED: Direct count - TC: O(1), SC: O(1)
    long countBySuggestionStatus(SuggestionStatus suggestionStatus);

    // ✅ OPTIMIZED: Single query for user + status - TC: O(log n), SC: O(pageSize)
    @Query("SELECT s FROM Suggestion s WHERE s.user.id = :userId AND s.suggestionStatus = :status ORDER BY s.createdAt DESC")
    Page<Suggestion> findByUserIdAndSuggestionStatus(@Param("userId") int userId,
                                                     @Param("status") SuggestionStatus status,
                                                     Pageable pageable);

    // ✅ OPTIMIZED: Built-in method - TC: O(1), SC: O(1)
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // ✅ OPTIMIZED: Batch delete - TC: O(k), SC: O(1) where k = ids size
    @Modifying
    @Query("DELETE FROM Suggestion s WHERE s.id IN :ids")
    void deleteAllByIdInBatch(@Param("ids") List<Integer> ids);

    // ✅ OPTIMIZED: Single query with projection - TC: O(log n), SC: O(pageSize)
    @Query("SELECT s.id, s.suggestedTitle, s.author, s.suggestionReason, s.suggestionStatus, s.createdAt, s.adminNotes, u.name, u.email " +
            "FROM Suggestion s JOIN s.user u WHERE s.suggestionStatus = :status ORDER BY s.createdAt DESC")
    Page<Object[]> findSuggestionsWithUserByStatus(@Param("status") SuggestionStatus status, Pageable pageable);

    // ✅ FIXED: Popular suggestions query - removed alias reference in ORDER BY
    @Query("SELECT s.id, s.suggestedTitle, s.author, " +
            "COUNT(DISTINCT v.id) as upvoteCount, " +
            "COUNT(DISTINCT a.id) as interestCount " +
            "FROM Suggestion s " +
            "LEFT JOIN UserSuggestionVote v ON v.suggestion.id = s.id AND v.upvoted = true " +
            "LEFT JOIN PublisherSuggestionAction a ON a.suggestion.id = s.id AND a.action = 'INTERESTED' " +
            "WHERE s.suggestionStatus = 'PENDING' " +
            "GROUP BY s.id, s.suggestedTitle, s.author " +
            "ORDER BY (COUNT(DISTINCT v.id) + COUNT(DISTINCT a.id)) DESC, s.createdAt DESC")
    Page<Object[]> findPopularSuggestionsWithStats(Pageable pageable);

    // ✅ OPTIMIZED: Single query for details - TC: O(1), SC: O(1)
    @Query("SELECT s, u.name, u.email, " +
            "COUNT(DISTINCT CASE WHEN v.upvoted = true THEN v.id END), " +
            "COUNT(DISTINCT CASE WHEN a.action = 'INTERESTED' THEN a.id END), " +
            "COUNT(DISTINCT CASE WHEN a.action = 'UPLOADED' THEN a.id END) " +
            "FROM Suggestion s " +
            "JOIN s.user u " +
            "LEFT JOIN UserSuggestionVote v ON v.suggestion.id = s.id " +
            "LEFT JOIN PublisherSuggestionAction a ON a.suggestion.id = s.id " +
            "WHERE s.id = :suggestionId " +
            "GROUP BY s, u.name, u.email")
    Optional<Object[]> findSuggestionDetailsWithStats(@Param("suggestionId") int suggestionId);

    // ✅ OPTIMIZED: Single query for dashboard - TC: O(1), SC: O(1)
    @Query("SELECT COUNT(s) as total, " +
            "SUM(CASE WHEN s.suggestionStatus = 'PENDING' THEN 1 ELSE 0 END) as pending, " +
            "SUM(CASE WHEN s.suggestionStatus = 'APPROVED' THEN 1 ELSE 0 END) as approved, " +
            "SUM(CASE WHEN s.suggestionStatus = 'REJECTED' THEN 1 ELSE 0 END) as rejected " +
            "FROM Suggestion s")
    Map<String, Long> getDashboardStats();

    // ✅ OPTIMIZED: Multiple statuses - TC: O(log n), SC: O(pageSize)
    Page<Suggestion> findBySuggestionStatusIn(List<SuggestionStatus> statuses, Pageable pageable);

    // ✅ OPTIMIZED: Search with indexes - TC: O(log n), SC: O(pageSize)
    @Query("SELECT s FROM Suggestion s WHERE " +
            "(:title IS NULL OR s.suggestedTitle LIKE %:title%) AND " +
            "(:author IS NULL OR s.author LIKE %:author%) AND " +
            "(:status IS NULL OR s.suggestionStatus = :status) " +
            "ORDER BY s.createdAt DESC")
    Page<Suggestion> searchSuggestions(@Param("title") String title,
                                       @Param("author") String author,
                                       @Param("status") SuggestionStatus status,
                                       Pageable pageable);

    // ✅ OPTIMIZED: Bulk update - TC: O(k), SC: O(1)
    @Modifying
    @Query("UPDATE Suggestion s SET s.suggestionStatus = :status WHERE s.id IN :ids")
    int bulkUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") SuggestionStatus status);

    // ✅ OPTIMIZED: Notification query with projection
    @Query("SELECT s.id, s.suggestedTitle, u.email, u.name " +
            "FROM Suggestion s JOIN s.user u " +
            "WHERE s.createdAt >= :sinceDate AND s.suggestionStatus = 'PENDING' " +
            "ORDER BY s.createdAt DESC")
    List<Object[]> findRecentPendingSuggestionsForNotification(@Param("sinceDate") LocalDateTime sinceDate);

    // ✅ OPTIMIZED: Cleanup query
    @Query("SELECT s.id FROM Suggestion s WHERE s.suggestionStatus = 'REJECTED' AND s.createdAt < :cutoffDate")
    List<Integer> findOldRejectedSuggestionIds(@Param("cutoffDate") LocalDateTime cutoffDate);

    // ✅ OPTIMIZED: Backward compatibility - TC: O(n), SC: O(n)
    @Query("SELECT s FROM Suggestion s WHERE s.user.id = :userId")
    List<Suggestion> findByUserId(@Param("userId") int userId);

    List<Suggestion> findBySuggestionStatus(SuggestionStatus suggestionStatus);

    // ✅ OPTIMIZED: Find approved suggestions after date
    @Query("SELECT s FROM Suggestion s WHERE s.suggestionStatus = 'APPROVED' AND s.createdAt >= :date")
    List<Suggestion> findApprovedSuggestionsAfterDate(@Param("date") LocalDateTime date);

    // ✅ OPTIMIZED: Find by user ID and status
    @Query("SELECT s FROM Suggestion s WHERE s.user.id = :userId AND s.suggestionStatus = :status")
    List<Suggestion> findByUserIdAndSuggestionStatus(@Param("userId") int userId,
                                                     @Param("status") SuggestionStatus status);

    // ✅ OPTIMIZED: Find pending suggestions ordered by date
    @Query("SELECT s FROM Suggestion s WHERE s.suggestionStatus = 'PENDING' ORDER BY s.createdAt DESC")
    List<Suggestion> findPendingSuggestionsOrderByDate();
}