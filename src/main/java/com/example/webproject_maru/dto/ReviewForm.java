package com.example.webproject_maru.dto;

import java.time.LocalDateTime;

import com.example.webproject_maru.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewForm {
    private Long id;
    private Long member_id;
    private Long article_id;
    private Integer score;
    private String score_reason;
    private LocalDateTime appendTime;
    private LocalDateTime updateTime;

 
    public static ReviewForm createReviewForm(Review review) {
        return new ReviewForm(
            review.getId(),
            review.getMember().getId(),
            review.getArticle().getId(),
            review.getScore(),
            review.getScore_reason(),
            review.getAppendTime(),
            review.getUpdateTime()
        );
    }
}
