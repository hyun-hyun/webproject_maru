package com.example.webproject_maru.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Member;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor //클래스 모든 필드의 생성자 만듬 this.title=title;
@ToString //클래스 모든 필드의 String으로 확인하는 것 오버라이딩
@Getter
public class ArticleForm {
    private Long id;
    private String catagory;
    private String title;
    private String genre;
    private LocalDate broad_date;
    private String ani_company;
    private String author;
    private Long memberId;

    //tag
    private List<String> tags;
    private String story;

    public Article toEntity(Member member) {
        // return new Article(id, catagory, title, genre, broad_date, null, story, null, null, null, null, null, null, null, null, null, null);


        return new Article(id, catagory, title, genre, broad_date, ani_company, author, story, member);
        // return new Article(id, catagory, title, genre, story);

    } 


}
