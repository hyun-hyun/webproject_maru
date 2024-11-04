package com.example.webproject_maru.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.webproject_maru.dto.ArticleForm;
import com.example.webproject_maru.dto.ArticleReviewDto;
import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.dto.TagCountDto;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.service.ArticleService;
import com.example.webproject_maru.service.Map_r_tService;
import com.example.webproject_maru.service.RecommendationService;
import com.example.webproject_maru.service.ReviewService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/mypage")
public class MyPageApiController {

    @Autowired
    private Map_r_tService map_r_tService;
    @Autowired
    private RecommendationService recommendationService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ReviewService reviewService;


    @GetMapping("/wordcloud/{memberId}")
    public ResponseEntity<List<TagCountDto>> getWordCloudData(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long memberId) {
        log.info("wordcloud api");
        Long member_id = userDetails.member.getId();
        // memberId와 member_id가 다를 경우 예외 처리
        if (!memberId.equals(member_id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access: memberId does not match.");
        }

        List<TagCountDto> tags = map_r_tService.countTagSelectionsByMemberId(memberId);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/myreview/{memberId}")
    public ResponseEntity<List<ArticleReviewDto>> getMyReviewArticles(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long memberId){
        Long member_id = userDetails.member.getId();
        // memberId와 로그인된 member_id가 다를 경우 예외 처리
        if (!memberId.equals(member_id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access: memberId does not match.");
        }
        //리뷰한 리뷰id
        List<Long> reviewedReviewIds=map_r_tService.getAllReviewIdByMemberId(memberId);
        List<ArticleReviewDto>articleReviewDtos=new ArrayList<>();
        log.info("hi");
        for(Long reviewId:reviewedReviewIds){
            ArticleReviewDto articleReviewDto=ArticleReviewDto.createArticleReviewDto(reviewService.findArticleByReviewId(reviewId), reviewService.findById(reviewId));
            if(articleReviewDto!=null){
                articleReviewDtos.add(articleReviewDto);
                log.info("controller dto추가 :articleId"+articleReviewDto.getArticleId());
            }
        }
        return ResponseEntity.ok(articleReviewDtos);

    }

    @GetMapping("/recommended-articles/{memberId}")
    public ResponseEntity<List<ArticleForm>> getRecommendedArticles(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long memberId) {
        log.info("추천 api 진입");
        Long member_id = userDetails.member.getId();
        // memberId와 member_id가 다를 경우 예외 처리
        if (!memberId.equals(member_id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access: memberId does not match.");
        }
        // 추천 게시글 ID 목록 가져오기
        List<Long> recommendedArticleIds = recommendationService.recommendArticleIds(memberId);

        // Article로 변환
        List<ArticleForm> articles = new ArrayList<>();
        for (Long articleId : recommendedArticleIds) {
            ArticleForm articleForm=ArticleForm.createArticleForm(articleService.findByIdArticle(articleId));
            //Article article = articleService.findByIdArticle(articleId);
            if (articleForm != null) {
                articles.add(articleForm);
            }
        }

        return ResponseEntity.ok(articles);
    }

    
}
