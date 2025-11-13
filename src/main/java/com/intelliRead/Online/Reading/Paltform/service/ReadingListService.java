package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.model.ReadingList;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.BookRepository;
import com.intelliRead.Online.Reading.Paltform.repository.ReadingListRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReadingListService {

    @Autowired
    private ReadingListRepository readingListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public String addToReadingList(int userId, int bookId) {
        try {
            // Check if already in reading list
            if (readingListRepository.existsByUserIdAndBookId(userId, bookId)) {
                return "Book is already in your reading list";
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            ReadingList readingList = new ReadingList();
            readingList.setUser(user);
            readingList.setBook(book);
            readingList.setStatus(ReadingList.ReadingStatus.WANT_TO_READ);
            readingList.setAddedDate(LocalDateTime.now());

            readingListRepository.save(readingList);
            return "Book added to reading list successfully";

        } catch (Exception e) {
            throw new RuntimeException("Error adding to reading list: " + e.getMessage());
        }
    }

    public String removeFromReadingList(int userId, int bookId) {
        try {
            Optional<ReadingList> readingList = readingListRepository.findByUserIdAndBookId(userId, bookId);
            if (readingList.isPresent()) {
                readingListRepository.delete(readingList.get());
                return "Book removed from reading list";
            } else {
                return "Book not found in reading list";
            }
        } catch (Exception e) {
            throw new RuntimeException("Error removing from reading list: " + e.getMessage());
        }
    }

    public String updateReadingStatus(int userId, int bookId, ReadingList.ReadingStatus status) {
        try {
            Optional<ReadingList> readingListOpt = readingListRepository.findByUserIdAndBookId(userId, bookId);
            if (readingListOpt.isPresent()) {
                ReadingList readingList = readingListOpt.get();
                readingList.setStatus(status);

                if (status == ReadingList.ReadingStatus.READING && readingList.getStartedReading() == null) {
                    readingList.setStartedReading(LocalDateTime.now());
                } else if (status == ReadingList.ReadingStatus.COMPLETED) {
                    readingList.setFinishedReading(LocalDateTime.now());
                }

                readingListRepository.save(readingList);
                return "Reading status updated successfully";
            } else {
                return "Book not found in reading list";
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating reading status: " + e.getMessage());
        }
    }

    public List<ReadingList> getUserReadingList(int userId) {
        return readingListRepository.findByUserId(userId);
    }

    public List<ReadingList> getUserReadingListByStatus(int userId, ReadingList.ReadingStatus status) {
        return readingListRepository.findByUserIdAndStatus(userId, status);
    }

    public boolean isBookInReadingList(int userId, int bookId) {
        return readingListRepository.existsByUserIdAndBookId(userId, bookId);
    }
}