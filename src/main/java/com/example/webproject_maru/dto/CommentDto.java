package com.example.webproject_maru.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.example.webproject_maru.entity.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private String body;
    private String nickname;
    private Long article_id;
    private Long parent_id;
    private List<CommentDto> replies;

    // Comment 객체로부터 생성
    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.body = comment.getBody();
        this.nickname = comment.getMember().getNickname();
        this.article_id = comment.getArticle().getId();
        this.parent_id = (comment.getParentComment() != null) ? comment.getParentComment().getId() : null;
        this.replies = (comment.getReplies() != null) ? comment.getReplies().stream()
                .map(reply -> new CommentDto(reply))
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    //답글 빈리스트로 초기화
    public CommentDto(CommentDto reply, List<Object> emptyList) {
        this.id = reply.getId();
        this.body = reply.getBody();
        this.nickname = reply.getNickname();
        this.article_id = reply.getArticle_id();
        this.parent_id = reply.getParent_id();
        // 빈 리스트를 받아서 새로운 replies 목록을 초기화함
        this.replies = emptyList != null ? new ArrayList<>() : new ArrayList<>();
    }

    public static CommentDto createCommentDto(Comment comment) {
        if (comment == null) return null;
        Long pId = (comment.getParentComment() == null) ? null : comment.getParentComment().getId();
        
        // reply 리스트를 CommentDto 리스트로 변환하여 전달
        List<CommentDto> replies = (comment.getReplies() != null) ? comment.getReplies().stream()
                .map(reply -> new CommentDto(reply))
                .collect(Collectors.toList()) : new ArrayList<>();
        
        return new CommentDto(
            comment.getId(),
            comment.getBody(),
            comment.getMember().getNickname(),
            comment.getArticle().getId(),
            pId,
            replies
        );
    }

            

}