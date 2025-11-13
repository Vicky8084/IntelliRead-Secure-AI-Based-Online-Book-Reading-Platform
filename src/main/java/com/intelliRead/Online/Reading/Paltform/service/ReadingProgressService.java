package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.model.ReadingProgress;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.BookRepository;
import com.intelliRead.Online.Reading.Paltform.repository.ReadingProgressRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReadingProgressService {

    @Autowired
    private ReadingProgressRepository readingProgressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public ReadingProgress updateReadingProgress(int userId, int bookId, int currentPage, int totalPages, int readingTimeMinutes) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            Optional<ReadingProgress> progressOpt = readingProgressRepository.findByUserIdAndBookId(userId, bookId);
            ReadingProgress progress;

            if (progressOpt.isPresent()) {
                progress = progressOpt.get();
            } else {
                progress = new ReadingProgress();
                progress.setUser(user);
                progress.setBook(book);
                progress.setCreatedAt(LocalDateTime.now());
            }

            progress.setCurrentPage(currentPage);
            progress.setTotalPages(totalPages);

            // Calculate progress percentage
            float percentage = 0.0f;
            if (totalPages > 0) {
                percentage = (float) currentPage / totalPages * 100;
                if (percentage > 100) percentage = 100.0f;
            }
            progress.setProgressPercentage(percentage);

            progress.setReadingTimeMinutes(progress.getReadingTimeMinutes() + readingTimeMinutes);
            progress.setLastRead(LocalDateTime.now());

            return readingProgressRepository.save(progress);

        } catch (Exception e) {
            throw new RuntimeException("Error updating reading progress: " + e.getMessage());
        }
    }

    public ReadingProgress getReadingProgress(int userId, int bookId) {
        return readingProgressRepository.findByUserIdAndBookId(userId, bookId)
                .orElse(null);
    }

    public String saveLastPosition(int userId, int bookId, String positionData) {
        try {
            Optional<ReadingProgress> progressOpt = readingProgressRepository.findByUserIdAndBookId(userId, bookId);
            if (progressOpt.isPresent()) {
                ReadingProgress progress = progressOpt.get();
                progress.setLastPosition(positionData);
                progress.setLastRead(LocalDateTime.now());
                readingProgressRepository.save(progress);
                return "Position saved successfully";
            } else {
                return "No reading progress found for this book";
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving position: " + e.getMessage());
        }
    }
}