package com.example.webproject_maru.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.SubPic;

public class ArticleDetailDto {
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
    private List<SubPic> subPics;
    private List<String> usedTags;//리뷰에서 사용된 태그
    private List<String> allTags;//게시글 전체 태그
    private List<TagCountDto> tagSelectionCounts;//태그별 사용된 수
    private List<ReviewDto> reviewDtos;//작성된 리뷰들
    private ReviewForm myReview;//나의 리뷰

    //상세보기용
    public ArticleDetailDto(Long id, String title, String category, String genre, LocalDate broaddate, String ani_company, String author, String main_pic, String story,
    Double avgscore, Long cscore, List<SubPic> subPics, List<String> usedTags, List<String> allTags, List<TagCountDto> tagSelectionCounts, List<ReviewDto> reviewDtos,ReviewForm myReview){
        this.id=id;
        this.title=title;
        this.category=category;
        this.genre=genre;
        this.broaddate=broaddate;
        this.ani_company=ani_company;
        this.author=author;
        this.main_pic=main_pic;
        this.story=story;
        this.avgscore=avgscore;
        this.cscore=cscore;
        this.subPics=subPics;
        this.usedTags=usedTags;
        this.allTags=allTags;
        this.tagSelectionCounts=tagSelectionCounts;
        this.reviewDtos=reviewDtos;
        this.myReview=myReview;
    }
    //수정용
    public ArticleDetailDto(Long id, String title, String category, String genre, LocalDate broaddate, String ani_company, String author, String main_pic, String story,
    Double avgscore, Long cscore, List<SubPic> subPics, List<String> usedTags, List<String> allTags){
        this.id=id;
        this.title=title;
        this.category=category;
        this.genre=genre;
        this.broaddate=broaddate;
        this.ani_company=ani_company;
        this.author=author;
        this.main_pic=main_pic;
        this.story=story;
        this.avgscore=avgscore;
        this.cscore=cscore;
        this.subPics=subPics;
        this.usedTags=usedTags;
        this.allTags=allTags;

    }

    //상세보기용
    public static ArticleDetailDto createArticleDetailDto(Article article, List<String> usedTags, List<String> allTags, List<TagCountDto> tagSelectionCounts, List<ReviewDto> reviewDtos, ReviewForm myReview){
        if(article==null){
            return null;
        }
        return new ArticleDetailDto(
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
            article.getSubPics(),
            usedTags,
            allTags,
            tagSelectionCounts,
            reviewDtos,
            myReview
        );
    }
    //수정용
    public static ArticleDetailDto createArticleDetailDto(Article article, List<String> usedTags, List<String> allTags){
        if(article==null){
            return null;
        }
        return new ArticleDetailDto(
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
            article.getSubPics(),
            usedTags,
            allTags
        );
    }
}
