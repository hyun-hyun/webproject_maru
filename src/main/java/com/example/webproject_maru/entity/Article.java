package com.example.webproject_maru.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String catagory;
    @Column(unique = true)
    private String title;
    @Column
    private String genre;
    private LocalDate broad_date;
    private String main_pic;
    private Integer score;
    private String score_reason;
    //tag
    private String story;
    private String sub_pic1;

    private String sub_pic2;

    private String sub_pic3;
    
    private String sub_pic4;
    
    private String sub_pic5;

    private String nickname;
    private LocalDateTime appendTime;
    private LocalDateTime updateTime;
    
    public Article (Long id, String catagory, String title, String genre, LocalDate broad_date, String story){
        this.id=id;
        this.catagory=catagory;
        this.title=title;
        this.genre=genre;
        this.broad_date=broad_date;
        // this.main_pic=main_pic;
        this.story=story;
    }
}
