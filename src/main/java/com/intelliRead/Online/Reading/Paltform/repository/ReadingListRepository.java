package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.model.ReadingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingListRepository extends JpaRepository<ReadingList, Integer> {

    List<ReadingList> findByUserId(int userId);

    List<ReadingList> findByUserIdAndStatus(int userId, ReadingList.ReadingStatus status);

    Optional<ReadingList> findByUserIdAndBookId(int userId, int bookId);

    boolean existsByUserIdAndBookId(int userId, int bookId);

    int countByUserIdAndStatus(int userId, ReadingList.ReadingStatus status);

    @Query("SELECT rl FROM ReadingList rl WHERE rl.user.id = :userId ORDER BY rl.addedDate DESC")
    List<ReadingList> findRecentByUserId(@Param("userId") int userId);
}