package com.example.webproject_maru.entity;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private String catagory;
    private String title;
    @Column
    private String genre;
    private LocalTime broad_date;
    private String main_pic_path;
    private Integer score;
    private String score_reason;
    //tag
    private String story;
    private String sub_pic1_path;
    private String sub_pic1_name;

    private String sub_pic2_path;
    private String sub_pic2_name;

    private String sub_pic3_path;
    private String sub_pic3_name;
    
    private String sub_pic4_path;
    private String sub_pic4_name;
    
    private String sub_pic5_path;
    private String sub_pic5_name;

}
