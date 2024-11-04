package com.example.webproject_maru.dto;

import java.util.List;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleReviewDto {
    private Long articleId;
    private String title;
    private String genre;
    private String main_pic;
    private Long reviewId;
    private Integer score;
    private String score_reason;

    public static ArticleReviewDto createArticleReviewDto(Article article, Review review){
        if(article==null){
            return null;
        }
        if(review==null){
            return null;
        }
        return new ArticleReviewDto(
            article.getId(),
            article.getTitle(),
            article.getGenre(),
            article.getMain_pic(),
            review.getId(),
            review.getScore(),
            review.getScore_reason()
        );
    }
}
