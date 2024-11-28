package com.example.webproject_maru.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="love")
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Love {
    @Id//대표키 지정
    @GeneratedValue(strategy=GenerationType.IDENTITY)//DB가 자동으로 1씩 증가
    private Long id;//대표키PK

    @ManyToOne(fetch = FetchType.LAZY)//Love n : Article 1 로  entity 관계설정
    @JoinColumn(name="article_id")//외래키 생성, Article 엔티티 기본키(id)와 매핑
    private Article article;//부모게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;//찜 한 사람

    public Love(Member member, Article article) {
        this.member = member;
        this.article = article;
    }
}
