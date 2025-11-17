package com.intelliRead.Online.Reading.Paltform.converter;

import com.intelliRead.Online.Reading.Paltform.model.Review;
import com.intelliRead.Online.Reading.Paltform.requestDTO.ReviewRequestDTO;
import com.intelliRead.Online.Reading.Paltform.responseDTO.ReviewResponseDTO;

public class ReviewConverter {

    public static Review convertReviewRequestDtoIntoReview(ReviewRequestDTO reviewRequestDTO){
        Review review = new Review();
        review.setRating(reviewRequestDTO.getRating());
        review.setReviewText(reviewRequestDTO.getReviewText());
        return review;
    }

    public static ReviewResponseDTO convertReviewIntoReviewResponseDto(Review review){
        ReviewResponseDTO responseDTO = new ReviewResponseDTO();
        responseDTO.setId(review.getId());
        responseDTO.setRating(review.getRating());
        responseDTO.setReviewText(review.getReviewText());
        responseDTO.setCreatedAt(review.getCreatedAt());

        // Add user information
        if(review.getUser() != null){
            responseDTO.setUserName(review.getUser().getName());
            responseDTO.setUserEmail(review.getUser().getEmail());
        }

        // Add book information
        if(review.getBook() != null){
            responseDTO.setBookId(review.getBook().getId());
            responseDTO.setBookTitle(review.getBook().getTitle());
        }

        return responseDTO;
    }
}