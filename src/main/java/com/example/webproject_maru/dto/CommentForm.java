package com.example.webproject_maru.dto;

import com.example.webproject_maru.entity.Comment;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentForm {
    private Long id;//댓글의D id
    private Long article_id;//댓글의 부모id
    private Long member_id;
    private String body;//댓글 본문
    private Long parent_id;//댓글부모(답글시)

    public static CommentForm createCommentForm(Comment comment) {
        if(comment==null) return null;
        Long p_id;
        if(comment.getParentComment()==null){
            p_id=null;
        }else{
            p_id=comment.getParentComment().getId();
        }
        return new CommentForm(
            comment.getId(),
            comment.getArticle().getId(),
            comment.getMember().getId(),
            comment.getBody(),
            p_id
        );
    }

}