package com.example.webproject_maru.dto;

import java.time.LocalDateTime;

import com.example.webproject_maru.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private Long id;
    private String member_nickname;
    private Long article_id;
    private Integer score;
    private String score_reason;
    private LocalDateTime appendTime;
    private LocalDateTime updateTime;
 
    public static ReviewDto createReviewDto(Review review) {
        if(review ==null){
            return null;
        }
        return new ReviewDto(
            review.getId(),
            review.getMember().getNickname(),
            review.getArticle().getId(),
            review.getScore(),
            review.getScore_reason(),
            review.getAppendTime(),
            review.getUpdateTime()
        );
    }
    
}
