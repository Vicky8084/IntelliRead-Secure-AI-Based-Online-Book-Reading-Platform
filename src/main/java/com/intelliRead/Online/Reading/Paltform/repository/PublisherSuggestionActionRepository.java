package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.enums.PublisherAction;
import com.intelliRead.Online.Reading.Paltform.model.PublisherSuggestionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherSuggestionActionRepository extends JpaRepository<PublisherSuggestionAction, Integer> {

    Optional<PublisherSuggestionAction> findByPublisherIdAndSuggestionId(int publisherId, int suggestionId);

    List<PublisherSuggestionAction> findBySuggestionId(int suggestionId);

    List<PublisherSuggestionAction> findByPublisherId(int publisherId);

    int countBySuggestionIdAndAction(int suggestionId, PublisherAction action);

    boolean existsByPublisherIdAndSuggestionIdAndAction(int publisherId, int suggestionId, PublisherAction action);

    @Query("SELECT p FROM PublisherSuggestionAction p WHERE p.action = :action AND p.suggestion.id = :suggestionId")
    List<PublisherSuggestionAction> findByActionAndSuggestionId(@Param("action") PublisherAction action, @Param("suggestionId") int suggestionId);

    @Query("SELECT COUNT(p) FROM PublisherSuggestionAction p WHERE p.suggestion.id = :suggestionId AND p.action IN :actions")
    int countBySuggestionIdAndActionIn(@Param("suggestionId") int suggestionId, @Param("actions") List<PublisherAction> actions);

    @Modifying
    @Query("DELETE FROM PublisherSuggestionAction p WHERE p.suggestion.id = :suggestionId")
    void deleteAllBySuggestionId(@Param("suggestionId") int suggestionId);

    @Query("SELECT p.suggestion.id, COUNT(p) FROM PublisherSuggestionAction p WHERE p.suggestion.id IN :suggestionIds AND p.action = 'INTERESTED' GROUP BY p.suggestion.id")
    List<Object[]> countInterestsBySuggestionIds(@Param("suggestionIds") List<Integer> suggestionIds);

    @Query("SELECT p FROM PublisherSuggestionAction p WHERE p.publisher.id = :publisherId AND p.suggestion.id IN :suggestionIds")
    List<PublisherSuggestionAction> findByPublisherIdAndSuggestionIdIn(@Param("publisherId") int publisherId, @Param("suggestionIds") List<Integer> suggestionIds);
}