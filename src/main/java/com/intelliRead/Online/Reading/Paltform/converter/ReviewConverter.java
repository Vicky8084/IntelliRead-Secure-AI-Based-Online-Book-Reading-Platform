package com.intelliRead.Online.Reading.Paltform.converter;

import com.intelliRead.Online.Reading.Paltform.model.Review;
import com.intelliRead.Online.Reading.Paltform.requestDTO.ReviewRequestDTO;

public class ReviewConverter {
    public static Review convertReviewRequestDtoIntoReview(ReviewRequestDTO reviewRequestDTO){
        Review review = new Review();
        review.setRating(reviewRequestDTO.getRating());
        review.setReviewText(reviewRequestDTO.getReviewText());
        return review;
    }
}