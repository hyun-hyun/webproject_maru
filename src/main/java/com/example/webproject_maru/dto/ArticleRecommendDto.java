package com.example.webproject_maru.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.webproject_maru.entity.Article;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ArticleRecommendDto {
    private Long id;
    private String title;
    private String genre;
    private LocalDate broad_date;
    private String ani_company;
    private String author;
    private String main_pic;
    private String story;
    private List<String> usedTags;//리뷰에서 사용된 태그

    public static ArticleRecommendDto createArticleRecommendDto(Article article, List<String> usedTags){
        if(article==null){
            return null;
        }
        return new ArticleRecommendDto(
            article.getId(),
            article.getTitle(),
            article.getGenre(),
            article.getBroad_date(),
            article.getAni_company(),
            article.getAuthor(),
            article.getMain_pic(),
            article.getStory(),
            usedTags
        );
    }
}
