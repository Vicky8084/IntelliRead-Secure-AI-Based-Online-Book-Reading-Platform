package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.model.ReadingList;
import com.intelliRead.Online.Reading.Paltform.model.ReadingProgress;
import com.intelliRead.Online.Reading.Paltform.repository.ReadingListRepository;
import com.intelliRead.Online.Reading.Paltform.repository.ReadingProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReadingAnalyticsService {

    @Autowired
    private ReadingListRepository readingListRepository;

    @Autowired
    private ReadingProgressRepository readingProgressRepository;

    public Map<String, Object> getUserReadingStats(int userId) {
        Map<String, Object> stats = new HashMap<>();

        // Reading list counts
        stats.put("wantToRead", readingListRepository.countByUserIdAndStatus(userId, ReadingList.ReadingStatus.WANT_TO_READ));
        stats.put("currentlyReading", readingListRepository.countByUserIdAndStatus(userId, ReadingList.ReadingStatus.READING));
        stats.put("completed", readingListRepository.countByUserIdAndStatus(userId, ReadingList.ReadingStatus.COMPLETED));

        // Reading progress stats
        List<ReadingProgress> allProgress = readingProgressRepository.findByUserId(userId);

        int totalReadingTime = allProgress.stream()
                .mapToInt(ReadingProgress::getReadingTimeMinutes)
                .sum();

        double averageProgress = allProgress.stream()
                .mapToDouble(ReadingProgress::getProgressPercentage)
                .average()
                .orElse(0.0);

        stats.put("totalReadingTimeMinutes", totalReadingTime);
        stats.put("averageProgress", Math.round(averageProgress * 100.0) / 100.0);
        stats.put("booksInProgress", allProgress.size());

        return stats;
    }

    public List<ReadingProgress> getRecentlyReadBooks(int userId) {
        return readingProgressRepository.findRecentProgressByUserId(userId);
    }

    public List<ReadingProgress> getNearlyCompletedBooks(int userId) {
        return readingProgressRepository.findNearlyCompletedBooks(userId);
    }
}