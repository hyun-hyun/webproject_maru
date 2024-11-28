package com.example.webproject_maru.dto;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleLoveDto {
    private Long articleId;
    private String title;
    private String main_pic;
    private String genre;
    private String story;

    public static ArticleLoveDto createArticleLoveDto(Article article){
        if(article==null){
            return null;
        }
        return new ArticleLoveDto(
            article.getId(),
            article.getTitle(),
            article.getMain_pic(),
            article.getGenre(),
            article.getStory()
        );
    }
}
