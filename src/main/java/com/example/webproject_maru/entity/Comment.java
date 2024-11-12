package com.example.webproject_maru.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.example.webproject_maru.dto.CommentDto;
import com.example.webproject_maru.dto.CommentForm;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity //이 클래스는 entity 선언, 바탕으로 DB에 테이블 생성
@Table(name="comment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id//대표키 지정
    @GeneratedValue(strategy=GenerationType.IDENTITY)//DB가 자동으로 1씩 증가
    private Long id;//대표키PK

    @ManyToOne//Comment n : Article 1 로  entity 관계설정
    @JoinColumn(name="article_id")//외래키 생성, Article 엔티티 기본키(id)와 매핑
    private Article article;//부모게시글

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;//댓글 단 사람

    @Column
    private String body;//댓글 내용

    @ManyToOne
    @JoinColumn(name="parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade=CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies=new ArrayList<>();

    @Column
    private LocalDateTime appendTime;
    @Column
    private LocalDateTime updateTime;
    
    // 댓글 생성 시 사용할 정적 메서드
    public static Comment createComment(CommentForm commentForm, Article article, Member member) {
        // 예외 발생 검증
        if (commentForm.getId() != null)
            throw new IllegalArgumentException("댓글 생성 실패! 댓글의 id가 없어야 합니다.");
        if (!commentForm.getArticle_id().equals(article.getId()))
            throw new IllegalArgumentException("댓글 생성 실패! 게시글의 id가 잘못됐습니다.");

        // 엔티티 생성 및 반환
        return new Comment(
            commentForm.getId(),
            article,
            member,
            commentForm.getBody()
        );
    }
    // 댓글 생성 시 사용할 정적 메서드
    public static Comment createReply(CommentForm commentForm, Article article, Member member, Comment parentComment) {
        // 예외 발생 검증
        if (commentForm.getId() != null)
            throw new IllegalArgumentException("댓글 생성 실패! 댓글의 id가 없어야 합니다.");
        if (!commentForm.getArticle_id().equals(article.getId()))
            throw new IllegalArgumentException("댓글 생성 실패! 게시글의 id가 잘못됐습니다.");

        // 엔티티 생성 및 반환
        return new Comment(
            commentForm.getId(),
            article,
            member,
            commentForm.getBody(),
            parentComment
        );
    }
    public Comment(Long id, Article article, Member member, String body){
        this.id=id;
        this.article=article;
        this.member=member;
        this.body=body;
    }
    public Comment(Long id, Article article, Member member, String body, Comment parentComment){
        this.id=id;
        this.article=article;
        this.member=member;
        this.body=body;
        this.parentComment=parentComment;
    }

    public void patch(CommentForm form) {
        //예외 발생
        if(this.id!=form.getId()) throw new IllegalArgumentException("댓글 수정 실패! 잘못된 id입력");
        //객체갱신
        if(form.getBody()!=null)//수정할 데이터 있다면
            this.body=form.getBody();
    }




}


