package com.intelliRead.Online.Reading.Paltform.controller;

import com.intelliRead.Online.Reading.Paltform.model.Review;
import com.intelliRead.Online.Reading.Paltform.requestDTO.ReviewRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review/apies")
public class ReviewController {
    ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveReview(@RequestBody ReviewRequestDTO reviewRequestDTO){
        try {
            String response = reviewService.saveReview(reviewRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findReviewById(@PathVariable int id){
        try {
            Review review = reviewService.findReviewById(id);
            if (review != null) {
                return ResponseEntity.ok(review);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Review>> findAllReview(){
        try {
            List<Review> reviews = reviewService.findAllReview();
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Review>> findReviewsByBookId(@PathVariable int bookId){
        try {
            List<Review> reviews = reviewService.findReviewsByBookId(bookId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> findReviewsByUserId(@PathVariable int userId){
        try {
            List<Review> reviews = reviewService.findReviewsByUserId(userId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateReview(@PathVariable int id, @RequestBody ReviewRequestDTO reviewRequestDTO){
        try {
            String response = reviewService.updateReview(id, reviewRequestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable int id){
        try {
            String response = reviewService.deleteReview(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}