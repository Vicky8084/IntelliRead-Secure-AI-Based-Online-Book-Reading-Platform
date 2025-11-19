package com.intelliRead.Online.Reading.Paltform.repository;

import com.intelliRead.Online.Reading.Paltform.model.DownloadHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DownloadHistoryRepository extends JpaRepository<DownloadHistory, Integer> {

    List<DownloadHistory> findByUserId(int userId);

    List<DownloadHistory> findByBookId(int bookId);

    Optional<DownloadHistory> findByUserIdAndBookId(int userId, int bookId);

    int countByBookId(int bookId);

    int countByUserId(int userId);

    @Query("SELECT COUNT(dh) FROM DownloadHistory dh WHERE dh.book.id = :bookId AND dh.user.id = :userId")
    int countDownloadsByUserAndBook(@Param("userId") int userId, @Param("bookId") int bookId);
}