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
import com.intelliRead.Online.Reading.Paltform.responseDTO.ReviewResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        // ✅ FIXED: Eagerly fetch user with all details
        User user = userRepository.findById(reviewRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + reviewRequestDTO.getUserId()));

        Book book = bookRepository.findById(reviewRequestDTO.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + reviewRequestDTO.getBookId()));

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

    // ✅ FIXED: Get reviews by book ID with user details
    public List<Review> findReviewsByBookId(int bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);

        // ✅ Eagerly load user details for each review
        reviews.forEach(review -> {
            if (review.getUser() != null) {
                // Force loading of user details
                User user = userRepository.findById(review.getUser().getId()).orElse(null);
                review.setUser(user);
            }
        });

        return reviews;
    }

    // ✅ Get reviews by user ID
    public List<Review> findReviewsByUserId(int userId) {
        return reviewRepository.findByUserId(userId);
    }

    // ✅ NEW: Get reviews for publisher's books
    public List<ReviewResponseDTO> findReviewsForPublisherBooks(int publisherId) {
        // First get all books by this publisher
        List<Book> publisherBooks = bookRepository.findByUserId(publisherId);

        // Then get all reviews for these books
        return publisherBooks.stream()
                .flatMap(book -> reviewRepository.findByBookId(book.getId()).stream())
                .map(ReviewConverter::convertReviewIntoReviewResponseDto)
                .collect(Collectors.toList());
    }

    // ✅ NEW: Get reviews for a specific book with publisher validation
    public List<ReviewResponseDTO> findReviewsForBookByPublisher(int bookId, int publisherId) {
        // Verify that the book belongs to this publisher
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (book.getUser().getId() != publisherId) {
            throw new IllegalArgumentException("You don't have permission to access reviews for this book");
        }

        return reviewRepository.findByBookId(bookId)
                .stream()
                .map(ReviewConverter::convertReviewIntoReviewResponseDto)
                .collect(Collectors.toList());
    }

    // ✅ NEW: Get book rating statistics for publisher dashboard
    public BookRatingStats getBookRatingStats(int bookId, int publisherId) {
        // Verify that the book belongs to this publisher
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (book.getUser().getId() != publisherId) {
            throw new IllegalArgumentException("You don't have permission to access this book's statistics");
        }

        List<Review> reviews = reviewRepository.findByBookId(bookId);

        if (reviews.isEmpty()) {
            return new BookRatingStats(0, 0.0, 0, 0, 0, 0, 0);
        }

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        long fiveStar = reviews.stream().filter(r -> r.getRating() == 5).count();
        long fourStar = reviews.stream().filter(r -> r.getRating() == 4).count();
        long threeStar = reviews.stream().filter(r -> r.getRating() == 3).count();
        long twoStar = reviews.stream().filter(r -> r.getRating() == 2).count();
        long oneStar = reviews.stream().filter(r -> r.getRating() == 1).count();

        return new BookRatingStats(
                reviews.size(),
                Math.round(averageRating * 10.0) / 10.0,
                fiveStar, fourStar, threeStar, twoStar, oneStar
        );
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

    // ✅ NEW: Inner class for rating statistics
    public static class BookRatingStats {
        private int totalReviews;
        private double averageRating;
        private long fiveStar;
        private long fourStar;
        private long threeStar;
        private long twoStar;
        private long oneStar;

        public BookRatingStats(int totalReviews, double averageRating,
                               long fiveStar, long fourStar, long threeStar,
                               long twoStar, long oneStar) {
            this.totalReviews = totalReviews;
            this.averageRating = averageRating;
            this.fiveStar = fiveStar;
            this.fourStar = fourStar;
            this.threeStar = threeStar;
            this.twoStar = twoStar;
            this.oneStar = oneStar;
        }

        // Getters and Setters
        public int getTotalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

        public long getFiveStar() { return fiveStar; }
        public void setFiveStar(long fiveStar) { this.fiveStar = fiveStar; }

        public long getFourStar() { return fourStar; }
        public void setFourStar(long fourStar) { this.fourStar = fourStar; }

        public long getThreeStar() { return threeStar; }
        public void setThreeStar(long threeStar) { this.threeStar = threeStar; }

        public long getTwoStar() { return twoStar; }
        public void setTwoStar(long twoStar) { this.twoStar = twoStar; }

        public long getOneStar() { return oneStar; }
        public void setOneStar(long oneStar) { this.oneStar = oneStar; }
    }
}