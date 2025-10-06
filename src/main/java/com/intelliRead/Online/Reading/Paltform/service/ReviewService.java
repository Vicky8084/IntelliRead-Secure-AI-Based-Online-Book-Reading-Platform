package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.ReviewConverter;
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
        this.reviewRepository=reviewRepository;
        this.userRepository=userRepository;
        this.bookRepository=bookRepository;

    }

    public String saveReview(ReviewRequestDTO reviewRequestDTO){
        Review review= ReviewConverter.convertReviewRequestDtoIntoReview(reviewRequestDTO);
        User user=userRepository.findById(reviewRequestDTO.getUserId()).get();
        Book book=bookRepository.findById(reviewRequestDTO.getBookId()).get();
        review.setBook(book);
        review.setUser(user);
        reviewRepository.save(review);
        return "Review Saved Successfully";
    }

    public Review findReviewById(int id){
        Optional<Review> reviewOptional=reviewRepository.findById(id);
        if(reviewOptional.isPresent()){
            return reviewOptional.get();
        }
        else {
            return null;
        }
    }

    public List<Review> findAllReview(){
        return reviewRepository.findAll();
    }

    public String updateReview(int id, ReviewRequestDTO reviewRequestDTO){
        Review review=findReviewById(id);
        if(review!=null){
            review.setRating(reviewRequestDTO.getRating());
            review.setReviewText(reviewRequestDTO.getReviewText());
            reviewRepository.save(review);
            return "Review Updated Successfully";
        }else{
            return "Review not found";
        }
    }

    public String deleteReview(int id){
        Review review=findReviewById(id);
        if(review!=null){
            reviewRepository.deleteById(id);
            return "Review Deleted Successfully";
        }
        else{
            return "review not found";
        }
    }
}
