package com.example.webproject_maru.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webproject_maru.dto.ArticleForm;
import com.example.webproject_maru.dto.TagCountDto;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.service.ArticleService;
import com.example.webproject_maru.service.Map_r_tService;
import com.example.webproject_maru.service.RecommendationService;

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


    @GetMapping("/wordcloud/{memberId}")
    public ResponseEntity<List<TagCountDto>> getWordCloudData(@PathVariable Long memberId) {
        log.info("wordcloud api");
        List<TagCountDto> tags = map_r_tService.countTagSelectionsByMemberId(memberId);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/recommended-articles/{memberId}")
    public ResponseEntity<List<ArticleForm>> getRecommendedArticles(@PathVariable Long memberId) {
        log.info("추천 api 진입");
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
