package com.example.webproject_maru.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Map_a_t {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//DB가 id 자동생성
    private Long id;

    @ManyToOne
    @JoinColumn(name="article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name="tag_id")
    private Tag tag;
}
