package com.example.webproject_maru.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.webproject_maru.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ArticleListDto {
    private Long id;
    private String title;
    private String category;
    private String genre;
    private LocalDate broaddate;
    private String ani_company;
    private String author;
    private String main_pic;
    private String story;
    private Double avgscore;
    private Long cscore;
    private List<String> usedTags;//리뷰에서 사용된 태그

    public static ArticleListDto createArticleListDto(Article article, List<String> usedTags){
        if(article==null){
            return null;
        }
        return new ArticleListDto(
            article.getId(),
            article.getTitle(),
            article.getCategory(),
            article.getGenre(),
            article.getBroaddate(),
            article.getAni_company(),
            article.getAuthor(),
            article.getMain_pic(),
            article.getStory(),
            article.getAvgscore(),
            article.getCscore(),
            usedTags
        );
    }
}
