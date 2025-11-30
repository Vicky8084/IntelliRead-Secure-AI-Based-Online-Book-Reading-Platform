package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.model.UserSuggestionVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSuggestionVoteRepository extends JpaRepository<UserSuggestionVote, Integer> {

    // ✅ OPTIMIZED: Single query with composite index - TC: O(1), SC: O(1)
    Optional<UserSuggestionVote> findByUserIdAndSuggestionId(int userId, int suggestionId);

    // ✅ OPTIMIZED: Use derived query with index - TC: O(k), SC: O(k) where k = result size
    List<UserSuggestionVote> findBySuggestionId(int suggestionId);

    // ✅ OPTIMIZED: Use derived query with index - TC: O(k), SC: O(k) where k = result size
    List<UserSuggestionVote> findByUserId(int userId);

    // ✅ OPTIMIZED: Use derived count query - TC: O(1), SC: O(1)
    int countBySuggestionIdAndUpvotedTrue(int suggestionId);

    // ✅ OPTIMIZED: Use derived exists query - TC: O(1), SC: O(1)
    boolean existsByUserIdAndSuggestionIdAndUpvotedTrue(int userId, int suggestionId);

    // ✅ OPTIMIZED: Remove redundant count query - use above method instead
    // @Query("SELECT COUNT(v) FROM UserSuggestionVote v WHERE v.suggestion.id = :suggestionId")
    // int countVotesBySuggestionId(@Param("suggestionId") int suggestionId);

    // ✅ OPTIMIZED: Remove duplicate delete method - use @Modifying version below
    // void deleteBySuggestionId(int suggestionId);

    // ✅ OPTIMIZED: Single batch delete operation - TC: O(k), SC: O(1) where k = affected rows
    @Modifying
    @Query("DELETE FROM UserSuggestionVote v WHERE v.suggestion.id = :suggestionId")
    void deleteAllBySuggestionId(@Param("suggestionId") int suggestionId);

    // ✅ NEW OPTIMIZED: Batch count for multiple suggestions - TC: O(m), SC: O(m) where m = suggestionIds size
    @Query("SELECT v.suggestion.id, COUNT(v) FROM UserSuggestionVote v WHERE v.suggestion.id IN :suggestionIds AND v.upvoted = true GROUP BY v.suggestion.id")
    List<Object[]> countUpvotesBySuggestionIds(@Param("suggestionIds") List<Integer> suggestionIds);

    // ✅ NEW OPTIMIZED: Batch check for user votes - TC: O(m), SC: O(m) where m = suggestionIds size
    @Query("SELECT v.suggestion.id FROM UserSuggestionVote v WHERE v.user.id = :userId AND v.suggestion.id IN :suggestionIds AND v.upvoted = true")
    List<Integer> findUpvotedSuggestionIdsByUser(@Param("userId") int userId, @Param("suggestionIds") List<Integer> suggestionIds);

    // ✅ NEW OPTIMIZED: Bulk insert for batch operations - TC: O(k), SC: O(1) where k = votes size
    @Modifying
    @Query(value = "INSERT INTO suggestion_votes (user_id, suggestion_id, upvoted, voted_at) VALUES (:userId, :suggestionId, true, NOW())", nativeQuery = true)
    void bulkInsertVote(@Param("userId") int userId, @Param("suggestionId") int suggestionId);

    // ✅ NEW OPTIMIZED: Bulk update for batch operations - TC: O(k), SC: O(1) where k = votes size
    @Modifying
    @Query("UPDATE UserSuggestionVote v SET v.upvoted = :upvoted, v.votedAt = CURRENT_TIMESTAMP WHERE v.user.id = :userId AND v.suggestion.id = :suggestionId")
    void bulkUpdateVote(@Param("userId") int userId, @Param("suggestionId") int suggestionId, @Param("upvoted") boolean upvoted);

    // ✅ NEW OPTIMIZED: Get vote statistics for dashboard - TC: O(1), SC: O(1)
    @Query("SELECT COUNT(v), SUM(CASE WHEN v.upvoted = true THEN 1 ELSE 0 END), SUM(CASE WHEN v.upvoted = false THEN 1 ELSE 0 END) FROM UserSuggestionVote v")
    Object[] getVoteStatistics();

    // ✅ NEW OPTIMIZED: Find recent votes with pagination - TC: O(log n), SC: O(pageSize)
    @Query("SELECT v FROM UserSuggestionVote v WHERE v.votedAt >= :sinceDate ORDER BY v.votedAt DESC")
    List<UserSuggestionVote> findRecentVotes(@Param("sinceDate") java.time.LocalDateTime sinceDate, org.springframework.data.domain.Pageable pageable);

    // ✅ NEW OPTIMIZED: Check multiple votes in single query - TC: O(m), SC: O(m) where m = suggestionIds size
    @Query("SELECT v.suggestion.id, v.upvoted FROM UserSuggestionVote v WHERE v.user.id = :userId AND v.suggestion.id IN :suggestionIds")
    List<Object[]> findUserVotesForSuggestions(@Param("userId") int userId, @Param("suggestionIds") List<Integer> suggestionIds);

    // ✅ NEW OPTIMIZED: Get top voted suggestions - TC: O(log n), SC: O(limit)
    @Query("SELECT v.suggestion.id, COUNT(v) as voteCount FROM UserSuggestionVote v WHERE v.upvoted = true GROUP BY v.suggestion.id ORDER BY voteCount DESC")
    List<Object[]> findTopVotedSuggestions(org.springframework.data.domain.Pageable pageable);

    // ✅ NEW OPTIMIZED: Cleanup old votes - TC: O(k), SC: O(1) where k = affected rows
    @Modifying
    @Query("DELETE FROM UserSuggestionVote v WHERE v.votedAt < :cutoffDate")
    int deleteOldVotes(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);

    // ✅ NEW OPTIMIZED: Get user voting activity - TC: O(1), SC: O(1)
    @Query("SELECT COUNT(v), MAX(v.votedAt) FROM UserSuggestionVote v WHERE v.user.id = :userId")
    Object[] getUserVotingActivity(@Param("userId") int userId);
}