package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.ReviewConverter;
import com.intelliRead.Online.Reading.Paltform.model.Review;
import com.intelliRead.Online.Reading.Paltform.repository.ReviewRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.ReviewRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    ReviewRepository reviewRepository;
    @Autowired
    public ReviewService(ReviewRepository reviewRepository){
        this.reviewRepository=reviewRepository;
    }

    public String saveReview(ReviewRequestDTO reviewRequestDTO){
        Review review= ReviewConverter.convertReviewRequestDtoIntoReview(reviewRequestDTO);
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
        review.setRating(reviewRequestDTO.getRating());
        review.setReviewText(reviewRequestDTO.getReviewText());
        reviewRepository.save(review);
        return "Review Updated Successfully";
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
