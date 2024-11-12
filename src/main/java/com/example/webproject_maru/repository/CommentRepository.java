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
}
