package com.example.webproject_maru.entity;

import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="map_a_t")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Map_a_t {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//DB가 id 자동생성
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="article_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tag_id")
    private Tag tag;

    public Map_a_t(Article article, Tag tag){
        this.article=article;
        this.tag=tag;
    }
}
