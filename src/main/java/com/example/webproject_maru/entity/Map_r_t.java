package com.example.webproject_maru.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Map_r_t {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//DB가 id 자동생성
    private Long id;

    @ManyToOne//지금 n:review 1
    @JoinColumn(name="article_id")
    private Review review;

    @ManyToOne
    @JoinColumn(name="tag_id")
    private Tag tag;

}
