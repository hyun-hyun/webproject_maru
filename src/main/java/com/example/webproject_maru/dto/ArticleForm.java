package com.example.webproject_maru.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Member;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor //클래스 모든 필드의 생성자 만듬 this.title=title;
@ToString //클래스 모든 필드의 String으로 확인하는 것 오버라이딩
@Getter
@Setter
public class ArticleForm {
    private Long id;
    private String category;
    private String title;
    private String genre;
    private LocalDate broad_date;
    private String ani_company;
    private String author;
    private Long memberId;

    private String main_pic;

    //tag
    private List<String> tags;//게시글내 전체태그 ->안씀
    private List<String> usedTags;//리뷰에서 사용한 태그 ->안씀
    private String story;

    public Article toEntity(Member member) {
        // return new Article(id, category, title, genre, broad_date, null, story, null, null, null, null, null, null, null, null, null, null);


        return new Article(id, category, title, genre, broad_date, ani_company, author, story, member);
        // return new Article(id, category, title, genre, story);

    } 

    public static ArticleForm createArticleForm(Article article){
        if(article ==null){
            return null;
        }
        return new ArticleForm(
            article.getId(), article.getCategory(), article.getTitle(), article.getGenre(), 
            article.getBroad_date(), article.getAni_company(), article.getAuthor(), article.getMember().getId(), article.getMain_pic(), article.getStory());
    }

    public ArticleForm(Long id, String category, String title, String genre, LocalDate broad_date, String ani_company, String author, Long memberId, String main_pic, String story){
        this.id=id;
        this.category=category;
        this.title=title;
        this.genre=genre;
        this.broad_date=broad_date;
        this.ani_company=ani_company;
        this.author=author;
        this.memberId=memberId;
        this.main_pic=main_pic;
        this.story=story;
    }


}
