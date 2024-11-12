package com.example.webproject_maru.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webproject_maru.dto.CommentDto;
import com.example.webproject_maru.dto.CommentForm;
import com.example.webproject_maru.service.CommentService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequestMapping("/api/comments")
public class CommentApiController {

    @Autowired
    private CommentService commentService;

    // 댓글 작성
    @PostMapping("/create_c")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentForm commentForm) {
        log.info("CommentApiController들어와서 createComment시작");
        
        CommentDto createdComment = commentService.create(commentForm);
        return ResponseEntity.status(HttpStatus.OK).body(createdComment);
    }

    //답글 작성
    @PostMapping("/reply")
    public ResponseEntity<CommentDto> createReply(@RequestBody CommentForm commentForm){
        log.info("CommentApiController들어와서 createReply~시작");
        CommentDto createdReply=commentService.createR(commentForm);
        return ResponseEntity.status(HttpStatus.OK).body(createdReply);
    }

    // 댓글 수정
    @PatchMapping("/edit/{commentId}")
    public ResponseEntity<CommentForm> editComment(@PathVariable Long commentId, @RequestBody CommentForm commentForm) {
        CommentForm updatedComment = commentService.update(commentId, commentForm);
        return ResponseEntity.status(HttpStatus.OK).body(updatedComment);
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<CommentDto> deleteComment(@PathVariable Long commentId) {
        CommentDto deletedForm=commentService.delete(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedForm);
    }

    // 특정 게시글의 댓글 목록 조회
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentDto>> getCommentsByArticle(@PathVariable Long articleId) {
        List<CommentDto> comments = commentService.getCommentsByArticle(articleId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }
}