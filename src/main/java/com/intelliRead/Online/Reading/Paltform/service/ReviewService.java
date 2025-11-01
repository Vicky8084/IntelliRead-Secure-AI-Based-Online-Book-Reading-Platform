package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.ReviewConverter;
import com.intelliRead.Online.Reading.Paltform.exception.BookNotFoundException;
import com.intelliRead.Online.Reading.Paltform.exception.ReviewAlreadyExistException;
import com.intelliRead.Online.Reading.Paltform.exception.UserNotFoundException;
import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.model.Review;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.BookRepository;
import com.intelliRead.Online.Reading.Paltform.repository.ReviewRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.ReviewRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    ReviewRepository reviewRepository;
    UserRepository userRepository;
    BookRepository bookRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         BookRepository bookRepository){
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public String saveReview(ReviewRequestDTO reviewRequestDTO){
        // ✅ Check if user already reviewed this book
        Optional<Review> existingReview = reviewRepository.findByUserIdAndBookId(
                reviewRequestDTO.getUserId(), reviewRequestDTO.getBookId());
        if (existingReview.isPresent()) {
            throw new ReviewAlreadyExistException("You have already reviewed this book!");
        }

        // ✅ Validate rating
        if (reviewRequestDTO.getRating() < 1 || reviewRequestDTO.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review review = ReviewConverter.convertReviewRequestDtoIntoReview(reviewRequestDTO);

        User user = userRepository.findById(reviewRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Book book = bookRepository.findById(reviewRequestDTO.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        review.setBook(book);
        review.setUser(user);
        reviewRepository.save(review);

        return "Review Saved Successfully";
    }

    public Review findReviewById(int id){
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        return reviewOptional.orElse(null);
    }

    public List<Review> findAllReview(){
        return reviewRepository.findAll();
    }

    // ✅ Get reviews by book ID
    public List<Review> findReviewsByBookId(int bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    // ✅ Get reviews by user ID
    public List<Review> findReviewsByUserId(int userId) {
        return reviewRepository.findByUserId(userId);
    }

    public String updateReview(int id, ReviewRequestDTO reviewRequestDTO){
        Review review = findReviewById(id);
        if (review != null) {
            // ✅ Validate rating
            if (reviewRequestDTO.getRating() < 1 || reviewRequestDTO.getRating() > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }

            review.setRating(reviewRequestDTO.getRating());
            review.setReviewText(reviewRequestDTO.getReviewText());
            reviewRepository.save(review);
            return "Review Updated Successfully";
        } else {
            throw new IllegalArgumentException("Review not found");
        }
    }

    public String deleteReview(int id){
        Review review = findReviewById(id);
        if (review != null) {
            reviewRepository.deleteById(id);
            return "Review Deleted Successfully";
        } else {
            throw new IllegalArgumentException("Review not found");
        }
    }
}