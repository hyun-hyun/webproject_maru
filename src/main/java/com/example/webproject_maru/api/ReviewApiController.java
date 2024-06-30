package com.example.webproject_maru.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webproject_maru.dto.ReviewForm;
import com.example.webproject_maru.service.ReviewService;

@RestController
public class ReviewApiController {
    @Autowired
    private ReviewService reviewService;


    @PostMapping("/articles/anime/{id}/create_r")
    public ResponseEntity<ReviewForm> createReview(ReviewForm reviewForm){
        
        ReviewForm saved=reviewService.create(reviewForm);
        return ResponseEntity.status(HttpStatus.OK).body(saved);

        // return "redirect:/articles/anime/{id}";
        
    }

}
