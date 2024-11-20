package com.example.webproject_maru.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.CommentDto;
import com.example.webproject_maru.dto.CommentForm;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Comment;
import com.example.webproject_maru.entity.Member;
import com.example.webproject_maru.repository.ArticleRepository;
import com.example.webproject_maru.repository.CommentRepository;
import com.example.webproject_maru.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private MemberRepository memberRepository;

    // 댓글 작성
    @Transactional
    public CommentDto create(CommentForm commentForm) {
        //1. 게시글 조회 및 예외 발생
        Article article=articleRepository.findById(commentForm.getArticle_id())
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패!"+
                        "대상 게시글이 없습니다."));//부모게시글 없으면 에러 메시지 출력
        Member member=memberRepository.findById(commentForm.getMember_id())
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패!"+
                        "대상 회원이 없습니다."));//작성자 없으면 에러 메시지 출력
        //2. 댓글 엔티티 생성
        Comment comment = Comment.createComment(commentForm, article, member);
        //3. 댓글 엔티티를 DB에 저장
        LocalDateTime SeoulNow = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        comment.setAppendTime(SeoulNow);
        comment.setUpdateTime(SeoulNow);
        Comment created=commentRepository.save(comment);
        //4. form 반환
        CommentDto resultC=CommentDto.createCommentDto(created);


        return resultC;
    }

    //대댓글 작성
    @Transactional
    public CommentDto createR(CommentForm commentForm){
        //1. 게시글 조회 및 예외 발생
        Article article=articleRepository.findById(commentForm.getArticle_id())
                .orElseThrow(() -> new IllegalArgumentException("답글 생성 실패!"+
                        "대상 게시글이 없습니다."));//부모게시글 없으면 에러 메시지 출력
        Member member=memberRepository.findById(commentForm.getMember_id())
                .orElseThrow(() -> new IllegalArgumentException("답글 생성 실패!"+
                        "대상 회원이 없습니다."));//작성자 없으면 에러 메시지 출력
        Comment parentComment = commentRepository.findById(commentForm.getParent_id())
                .orElseThrow(()->new IllegalArgumentException("답글 생성 실패!"+
                "부모 댓글이 없습니다."));
        //2. 답글 엔티티 생성
        Comment reply = Comment.createReply(commentForm, article, member, parentComment);
        //3. 답글 엔티티를 DB에 저장
        LocalDateTime SeoulNow = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        reply.setAppendTime(SeoulNow);
        reply.setUpdateTime(SeoulNow);
        Comment created=commentRepository.save(reply);
        //4. form 반환
        CommentDto resultC=CommentDto.createCommentDto(created);

        return resultC;
    }

    // 댓글 수정
    @Transactional
    public CommentDto update(Long commentId, CommentForm commentForm) {
        log.info("update들어옴 id: "+commentId);
        //댓글 조회 및 예외발생
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new IllegalArgumentException("댓글 수정 실패!"+"대상 댓글이 없습니다."));
        log.info("객체가져옴");
        //객체수정
        comment.patch(commentForm);
        log.info("수정완료");
        //updateTime 갱신
        LocalDateTime SeoulNow=LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        comment.setUpdateTime(SeoulNow);
        //DB갱신
        Comment updated=commentRepository.save(comment);
        return CommentDto.createCommentDto(updated);
    }

    // 댓글 삭제
    @Transactional
    public CommentDto delete(Long commentId) {
        //댓글 조회 및 예외발생
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new IllegalArgumentException("댓글 수정 실패!"+"대상 댓글이 없습니다."));
        //밑에 답글있으면 body만 삭제된 댓글입니다로 변경

        //댓글삭제
        commentRepository.delete(comment);
        return CommentDto.createCommentDto(comment);
    }

    // 특정 게시글에 대한 댓글 목록 조회
    public List<CommentDto> getCommentsByArticle(Long articleId) {
        List<Comment> comments = commentRepository.findByArticle_id(articleId);
        return comments.stream().map(CommentDto::new).collect(Collectors.toList());
    }

    //댓글조회(존재여부만)
    public boolean existsByArticleId(Long articleId){
        return commentRepository.existsByArticleId(articleId);
    }

    //댓글삭제(articleId기준으로 한번에)
    @Transactional
    public void deleteByArticleId(Long articleId){
        commentRepository.deleteByArticleId(articleId);
    }

    //댓글조회
    public CommentDto getCommentById(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        return comment.map(CommentDto::new).orElse(null);
    }
}
