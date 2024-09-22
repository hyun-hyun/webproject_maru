package com.example.webproject_maru.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Map_r_t {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//DB가 id 자동생성
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)//지금 n:review 1
    @JoinColumn(name="review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tag_id")
    private Tag tag;


    public Map_r_t(Review review, Tag tag){
        this.review=review;
        this.tag=tag;
    }

}
