package com.example.webproject_maru.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.webproject_maru.dto.ReviewForm;
import com.example.webproject_maru.service.ArticleService;
import com.example.webproject_maru.service.ReviewService;

import lombok.extern.slf4j.Slf4j;

@Slf4j //로깅
@RestController
public class ReviewApiController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/api/articles/{id}/create_r")
    public ResponseEntity<ReviewForm> createReview(@PathVariable Long id, @RequestBody ReviewForm reviewForm){
        log.info("ReviewApiController들어와서 createReview시작");
        ReviewForm saved=reviewService.create(reviewForm);

        return ResponseEntity.status(HttpStatus.OK).body(saved);
        
    }

}
