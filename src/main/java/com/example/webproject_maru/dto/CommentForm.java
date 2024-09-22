package com.example.webproject_maru.dto;

import com.example.webproject_maru.entity.Comment;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CommentForm {
    private Long id;//댓글의 id
    private Long articleId;//댓글의 부모id
    private String nickname;//댓글 작성자
    private String body;//댓글 본문
    private LocalDateTime appendTime;
    private LocalDateTime updateTime;
    public static CommentForm createCommentDto(Comment comment) {
        return new CommentForm(
            comment.getId(),//댓글 엔티티의 id
            comment.getArticle().getId(),//댓글 엔티티가 속한 부모 게시글의 id
            comment.getMember().getNickname(),//댓글 엔티티의 nickname
            comment.getBody(),//댓글 엔티티의 body,
            comment.getAppendTime(),
            comment.getUpdateTime()
        );
    }
}