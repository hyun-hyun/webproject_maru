package com.example.webproject_maru.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.ReviewForm;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Member;
import com.example.webproject_maru.entity.Review;
import com.example.webproject_maru.repository.ArticleRepository;
import com.example.webproject_maru.repository.MemberRepository;
import com.example.webproject_maru.repository.ReviewRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j //로깅
@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private MemberRepository memberRepository;


    @Transactional
    public ReviewForm create(ReviewForm dto){
        //1. 게시글 조회 및 예외 발생
        Article article=articleRepository.findById(dto.getArticle_id())
                .orElseThrow(() -> new IllegalArgumentException("리뷰 생성 실패!"+
                        "대상 게시글이 없습니다."));//부모게시글 없으면 에러 메시지 출력
        Member member=memberRepository.findById(dto.getMember_id())
                .orElseThrow(() -> new IllegalArgumentException("리뷰 생성 실패!"+
                        "대상 회원이 없습니다."));//작성자 없으면 에러 메시지 출력
        //2. 리뷰 엔티티 생성
        Review review=Review.createReview(dto, member, article);
        //3. 댓글 엔티티를 DB에 저장
        LocalDateTime SeoulNow = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        review.setAppendTime(SeoulNow);
        review.setUpdateTime(SeoulNow);
        Review created=reviewRepository.save(review);
        //4. DTO로 변환해 반환
        return ReviewForm.createReviewForm(created);
    }
}
