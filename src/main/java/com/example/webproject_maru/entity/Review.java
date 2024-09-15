package com.example.webproject_maru.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.webproject_maru.dto.ReviewForm;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Column
    private Integer score;
    @Column
    private String score_reason;
    @Column
    private LocalDateTime appendTime;
    @Column
    private LocalDateTime updateTime;
    
    //tag
    @OneToMany(mappedBy = "review", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Map_r_t> map_r_ts=new ArrayList<>();

    public static Review createReview(ReviewForm dto, Member member, Article article) {//static이므로 객체생성 안해도 호출가능
        //예외 발생
        if(dto.getId()!=null)
            throw new IllegalArgumentException("리뷰 생성 실패! 리뷰의 id가 없어야 합니다.");
        if(dto.getMember_id()==null)
            throw new IllegalArgumentException("리뷰 생성 실패! 회원의 id가 있어야 합니다.");
        if(dto.getArticle_id()!=article.getId())
            throw new IllegalArgumentException("리뷰 생성 실패! 게시글의 id가 잘못됐습니다.");
        //엔티티 생성 및 반환
        return new Review(
            dto.getId(),
            member,
            article,
            dto.getScore(),
            dto.getScore_reason(),
            dto.getAppendTime(),
            dto.getUpdateTime()
        );
    }
    public void patch(ReviewForm dto) {
        //예외 발생
        if(this.id != dto.getId())
            throw new IllegalArgumentException("리뷰 수정 실패! 잘못된 id가 입력됐습니다.");
        //객체 갱신
        if(dto.getScore()!=null)//수정할 점수가 있다면
            this.score=dto.getScore();//내용 반영
        if(dto.getScore_reason()!=null)//수정할 한줄평이 있다면
            this.score_reason=dto.getScore_reason();//내용 반영
    }
    public Review(Long id, Member member, Article article, Integer score, String score_reason,
            LocalDateTime appendTime, LocalDateTime updateTime) {
        this.id=id;
        this.member=member;
        this.article=article;
        this.score=score;
        this.score_reason=score_reason;
        this.appendTime=appendTime;
        this.updateTime=updateTime;
    }

}
