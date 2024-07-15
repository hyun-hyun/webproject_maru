package com.example.webproject_maru.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Member;
import com.example.webproject_maru.entity.SubPic;

import jakarta.persistence.Embedded;
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
    private Long memberId;

    //tag
    private String story;

    // @Embedded
    // private SubPicForm subpic1Form;

    // @Embedded
    // private SubPic subPic1;
    // @Embedded
    // private SubPic subPic2;
    // @Embedded
    // private SubPic subPic3;
    // @Embedded
    // private SubPic subPic4;
    // @Embedded
    // private SubPic subPic5;


    public Article toEntity(Member member) {
        // return new Article(id, catagory, title, genre, broad_date, null, story, null, null, null, null, null, null, null, null, null, null);


        return new Article(id, catagory, title, genre, broad_date, story, member);
        // return new Article(id, catagory, title, genre, story);

    } 


}
