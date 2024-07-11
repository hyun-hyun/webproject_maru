package com.example.webproject_maru.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="article_id")
    private Long id;
    @Column
    private String catagory;
    @Column(unique = true)
    private String title;
    @Column
    private String genre;
    @Column
    private LocalDate broad_date;
    @Column
    private String main_pic;
    @Column
    private String main_pic_name;

    @Column
    private Integer score;
    @Column
    private String score_reason;
    @Column
    private Double avg_score=0.0;
    @Column
    private Integer c_score=0;

    //tag
    @Column
    private String story;
    @Column
    private String sub_pic1;
    @Column
    private String sub_pic1_name;

    @Column
    private String sub_pic2;
    @Column
    private String sub_pic2_name;

    @Column
    private String sub_pic3;
    @Column
    private String sub_pic3_name;
    
    @Column
    private String sub_pic4;
    @Column
    private String sub_pic4_name;
    
    @Column
    private String sub_pic5;
    @Column
    private String sub_pic5_name;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private LocalDateTime appendTime;
    @Column
    private LocalDateTime updateTime;
    
    // public Article (Long id, String catagory, String title, String genre, String story){

    public Article (Long id, String catagory, String title, String genre, LocalDate broad_date, String story, Member member){
        this.id=id;
        this.catagory=catagory;
        this.title=title;
        this.genre=genre;
        this.broad_date=broad_date;
        // this.main_pic=main_pic;
        this.story=story;
        this.member=member;
    }
}
