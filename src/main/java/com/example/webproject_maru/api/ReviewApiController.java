package com.example.webproject_maru.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    //리뷰생성
    @PostMapping("/api/articles/{id}/create_r")
    public ResponseEntity<ReviewForm> createReview(@PathVariable Long id, @RequestBody ReviewForm reviewForm){
        log.info("ReviewApiController들어와서 createReview시작");
        ReviewForm saved=reviewService.create(reviewForm);

        return ResponseEntity.status(HttpStatus.OK).body(saved);
        
    }

    //리뷰수정
    @PatchMapping("/api/reviews/{id}")
    public ResponseEntity<ReviewForm> update(@PathVariable Long id, @RequestBody ReviewForm reviewForm){
        //서비스에 위임
        ReviewForm updatedForm=reviewService.update(id, reviewForm);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(updatedForm);
    }

    //댓글 삭제
    @DeleteMapping("/api/reviews/{articleId}/{id}")
    public ResponseEntity<ReviewForm> delte(@PathVariable Long id, @PathVariable Long articleId){
        //서비스에 위임
        ReviewForm deletedForm=reviewService.delete(id, articleId);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(deletedForm);
    }
}
