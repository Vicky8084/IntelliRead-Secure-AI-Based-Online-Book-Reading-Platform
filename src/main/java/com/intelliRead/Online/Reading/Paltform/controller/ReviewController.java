package com.intelliRead.Online.Reading.Paltform.controller;


import com.intelliRead.Online.Reading.Paltform.model.Review;
import com.intelliRead.Online.Reading.Paltform.requestDTO.ReviewRequestDTO;
import com.intelliRead.Online.Reading.Paltform.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review/apies")
public class ReviewController {
    ReviewService reviewService;
    @Autowired
    public ReviewController(ReviewService reviewService){
        this.reviewService=reviewService;
    }

    @PostMapping("/save")
    public String saveReview(@RequestBody ReviewRequestDTO reviewRequestDTO){
        return reviewService.saveReview(reviewRequestDTO);
    }

    @GetMapping("/findById/{id}")
    public Review findReviewById(@PathVariable int id){
        return reviewService.findReviewById(id);
    }

    @GetMapping("/FindAll")
    public List<Review> findAllReview(){
        return reviewService.findAllReview();
    }

    @PutMapping("/update/{id}")
    public String updateReview(@PathVariable int id, @RequestBody ReviewRequestDTO reviewRequestDTO){
        return reviewService.updateReview(id,reviewRequestDTO);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteReview(@PathVariable int id){
        return reviewService.deleteReview(id);
    }
}
