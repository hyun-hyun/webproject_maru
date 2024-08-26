package com.example.webproject_maru.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.ReviewForm;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Map_r_t;
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

    //리뷰 조회(전체)
    public List<ReviewForm> reviews(Long articleId){
        return reviewRepository.findByArticleId(articleId)//리뷰 엔터티 목록 조회
                .stream()//댓글 엔티티 목록을 스트림으로 변환
                .map(review -> ReviewForm.createReviewForm(review))//엔티티를 DTO로 매핑
                .collect(Collectors.toList());//스트림을 리스트 자료형으로 변환
    }

    //리뷰 조회(로그인한 사용자)
    public ReviewForm my_review(Long articleId, Long memberId){
        return ReviewForm.createReviewForm(reviewRepository.findByArticleMemberId(articleId, memberId));
    }

    //리뷰 생성
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
        //3. 리뷰 엔티티를 DB에 저장
        LocalDateTime SeoulNow = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        review.setAppendTime(SeoulNow);
        review.setUpdateTime(SeoulNow);
        Review created=reviewRepository.save(review);
        //리뷰 개수 갱신(article 값)
        Integer t_c_score=article.getC_score();
        article.setC_score(t_c_score+1);
        //리뷰 평균 갱신(article 값)
        Double t_avg_score=reviewRepository.getScoreAverage(dto.getArticle_id());
        article.setAvg_score(t_avg_score !=null ? (double)Math.round(t_avg_score*10.0)/10.0 : 0.0);
        articleRepository.save(article);
        //4. DTO로 변환해 반환
        return ReviewForm.createReviewForm(created);
    }

    //리뷰 수정
    @Transactional
    public ReviewForm update(Long id, ReviewForm dto){
        //1. 리뷰 조회 및 예외 발생
        Review target=reviewRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("리뷰 수정 실패!"+"대상 리뷰가 없습니다."));
        //2. 리뷰 수정
        target.patch(dto);
        //updateTime 갱신
        LocalDateTime SeoulNow=LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        target.setUpdateTime(SeoulNow);
        //3. DB로 갱신
        Review updated=reviewRepository.save(target);
        //4. 리뷰 엔티티를 dto로 변환 및 반환
        return ReviewForm.createReviewForm(updated);

    }

    //리뷰 삭제
    @Transactional
    public ReviewForm delete(Long id, Long articleId){
        //1.리뷰 조회 및 예외발생
        Review target=reviewRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("리뷰 삭제 실패!"+"대상이 없습니다."));
        //2. 리뷰 삭제
        reviewRepository.delete(target);
        //2-2. article t_c_score랑 t_avg_score 갱신
        Article article=articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 생성 실패!"+
                        "대상 게시글이 없습니다."));//부모게시글 없으면 에러 메시지 출력
        Integer t_c_score=article.getC_score();
        article.setC_score(t_c_score-1);
        //리뷰 평균 갱신(article 값)
        Double t_avg_score=reviewRepository.getScoreAverage(articleId);
        article.setAvg_score(t_avg_score !=null ? (double)Math.round(t_avg_score*10.0)/10.0 : 0.0);
        articleRepository.save(article);

        //3. 삭제 리뷰를 dto로 변환 및 반환
        return ReviewForm.createReviewForm(target);
    }
/*
    //articleId에 따른 tag 선택된거 조회
    public List<Map_r_t> getReviewTagsByArticleId(Long articleId){
        return reviewRepository.findTagsByArticleId(articleId);
    }
    //memberId에 따른 tag조회
    public List<Map_r_t> getReviewTagsByMemberId(Long memberId){
        return reviewRepository.findTagsByMemberId(memberId);
    }*/
}
