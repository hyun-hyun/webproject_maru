package com.example.webproject_maru.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.webproject_maru.entity.Map_r_t;
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
    private String member_nickname;
    private Long article_id;
    private Integer score;
    private String score_reason;
    private LocalDateTime appendTime;
    private LocalDateTime updateTime;
    private List<String> selectingTags;
 
    public static ReviewForm createReviewForm(Review review) {
        if(review ==null){
            return null;
        }
        return new ReviewForm(
            review.getId(),
            review.getMember().getId(),
            review.getMember().getNickname(),
            review.getArticle().getId(),
            review.getScore(),
            review.getScore_reason(),
            review.getAppendTime(),
            review.getUpdateTime()
        );
    }

    public ReviewForm(Long id, Long member_id, String member_nickname, Long article_id, Integer score, String score_reason,
            LocalDateTime appendTime, LocalDateTime updateTime) {
        this.id=id;
        this.member_id=member_id;
        this.member_nickname=member_nickname;
        this.article_id=article_id;
        this.score=score;
        this.score_reason=score_reason;
        this.appendTime=appendTime;
        this.updateTime=updateTime;

    }
}
