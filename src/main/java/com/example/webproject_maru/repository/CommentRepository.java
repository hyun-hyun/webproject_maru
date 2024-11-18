package com.example.webproject_maru.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.webproject_maru.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByArticle_id(Long articleId);
    @Modifying
    @Query("UPDATE Comment c SET c.body = :body WHERE c.id = :commentId")
    void updateCommentContent(@Param("commentId") Long commentId, @Param("body") String body);

    //특정게시글의 댓글 존재여부 조회
    @Query("SELECT COUNT(c) >0 FROM Comment c WHERE c.article.id = :articleId")
    boolean existsByArticleId(@Param("articleId") Long articleId);
    //articleId로 댓글 삭제
    void deleteByArticleId(Long articleId);
}
