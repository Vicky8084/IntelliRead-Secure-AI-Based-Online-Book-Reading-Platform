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

    Optional<UserSuggestionVote> findByUserIdAndSuggestionId(int userId, int suggestionId);

    List<UserSuggestionVote> findBySuggestionId(int suggestionId);

    List<UserSuggestionVote> findByUserId(int userId);

    int countBySuggestionIdAndUpvotedTrue(int suggestionId);

    boolean existsByUserIdAndSuggestionIdAndUpvotedTrue(int userId, int suggestionId);

    @Query("SELECT COUNT(v) FROM UserSuggestionVote v WHERE v.suggestion.id = :suggestionId")
    int countVotesBySuggestionId(@Param("suggestionId") int suggestionId);
    void deleteBySuggestionId(int suggestionId);

    @Modifying
    @Query("DELETE FROM UserSuggestionVote v WHERE v.suggestion.id = :suggestionId")
    void deleteAllBySuggestionId(@Param("suggestionId") int suggestionId);
}