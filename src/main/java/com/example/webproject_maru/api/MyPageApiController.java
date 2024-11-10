package com.example.webproject_maru.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.webproject_maru.dto.ArticleForm;
import com.example.webproject_maru.dto.ArticleRecommendDto;
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "유효하지 않은 접근입니다.");
        }

        List<TagCountDto> tags = map_r_tService.countTagSelectionsByMemberId(memberId);
        return ResponseEntity.ok(tags);
    }

    //일부
    @GetMapping("/myreviewOnly/{memberId}")
    public ResponseEntity<List<ArticleReviewDto>> getMyReviewOnlyArticles(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long memberId){
        Long member_id = userDetails.member.getId();
        // memberId와 로그인된 member_id가 다를 경우 예외 처리
        if (!memberId.equals(member_id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "유효하지 않은 접근입니다.");
        }

        //리뷰한 리뷰id
        List<Long> reviewedReviewIds=reviewService.getAllReviewIdByMemberId(memberId);

        List<ArticleReviewDto>articleReviewDtos=new ArrayList<>();
        for(Long reviewId:reviewedReviewIds){
            ArticleReviewDto articleReviewDto=ArticleReviewDto.createArticleReviewDto(reviewService.findArticleByReviewId(reviewId), reviewService.findById(reviewId));
            if(articleReviewDto!=null){
                articleReviewDtos.add(articleReviewDto);
            }
        }

        return ResponseEntity.ok(articleReviewDtos);

    }

    //전체
    @GetMapping("/myreview/{memberId}")
    public ResponseEntity<List<ArticleReviewDto>> getMyReviewArticles(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long memberId, @RequestParam("page") int page){
        Long member_id = userDetails.member.getId();
        // memberId와 로그인된 member_id가 다를 경우 예외 처리
        if (!memberId.equals(member_id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "유효하지 않은 접근입니다.");
        }

        //리뷰한 리뷰id
        List<Long> reviewedReviewIds=reviewService.getAllReviewIdByMemberId(memberId);

        // 페이징 처리: 요청된 페이지 번호에 맞는 게시글 반환
        int size=20;
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, reviewedReviewIds.size());

        // 범위를 벗어나지 않도록 처리
        if (fromIndex >= reviewedReviewIds.size()) {
            return ResponseEntity.ok(new ArrayList<>()); // 페이지가 범위를 초과할 경우 빈 리스트 반환
        }

        // 해당 페이지의 게시글 ID 서브리스트 생성
        List<Long> pagedArticleIds = reviewedReviewIds.subList(fromIndex, toIndex);

        List<ArticleReviewDto>articleReviewDtos=new ArrayList<>();
        for(Long reviewId:pagedArticleIds){
            ArticleReviewDto articleReviewDto=ArticleReviewDto.createArticleReviewDto(reviewService.findArticleByReviewId(reviewId), reviewService.findById(reviewId));
            if(articleReviewDto!=null){
                articleReviewDtos.add(articleReviewDto);
                log.info("controller dto추가 :articleId"+articleReviewDto.getArticleId());
            }
        }

        return ResponseEntity.ok(articleReviewDtos);

    }

    @GetMapping("/recommended-articles/{memberId}")
    public ResponseEntity<List<ArticleRecommendDto>> getRecommendedArticles(
            @AuthenticationPrincipal CustomUserDetails userDetails, 
            @PathVariable Long memberId,
            @RequestParam("page") int page) {
        
        Long member_id = userDetails.member.getId();
        // memberId와 member_id가 다를 경우 예외 처리
        if (!memberId.equals(member_id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "유효하지 않은 접근입니다.");
        }

        // 추천 게시글 ID 목록 가져오기
        List<Long> recommendedArticleIds = recommendationService.recommendArticleIds(memberId);

        // 페이징 처리: 요청된 페이지 번호에 맞는 게시글 반환
        int size=20;
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, recommendedArticleIds.size());

        // 범위를 벗어나지 않도록 처리
        if (fromIndex >= recommendedArticleIds.size()) {
            return ResponseEntity.ok(new ArrayList<>()); // 페이지가 범위를 초과할 경우 빈 리스트 반환
        }

        // 해당 페이지의 게시글 ID 서브리스트 생성
        List<Long> pagedArticleIds = recommendedArticleIds.subList(fromIndex, toIndex);

        // Article로 변환
        List<ArticleRecommendDto> articleRecommendDtos = new ArrayList<>();
        for (Long articleId : pagedArticleIds) {
            List<String> usedTags = map_r_tService.getOnlyTagsByArticleId(articleId); // 태그
            ArticleRecommendDto articleRecommendDto = ArticleRecommendDto.createArticleRecommendDto(
                    articleService.findByIdArticle(articleId), usedTags);
            if (articleRecommendDto != null) {
                articleRecommendDtos.add(articleRecommendDto);
            }
        }

        return ResponseEntity.ok(articleRecommendDtos);
    }


    
}
