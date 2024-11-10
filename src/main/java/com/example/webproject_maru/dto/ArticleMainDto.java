package com.example.webproject_maru.dto;

import java.util.List;

import com.example.webproject_maru.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ArticleMainDto {
    private Long id;
    private String title;
    private String genre;
    private String main_pic;
    private String story;
    private List<String> usedTags;//리뷰에서 사용된 태그

    public static ArticleMainDto createArticleMainDto(Article article, List<String> usedTags){
        if(article==null){
            return null;
        }
        return new ArticleMainDto(
            article.getId(),
            article.getTitle(),
            article.getGenre(),
            article.getMain_pic(),
            article.getStory(),
            usedTags
        );
    }
}
