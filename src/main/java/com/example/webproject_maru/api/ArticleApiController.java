package com.example.webproject_maru.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.webproject_maru.dto.ArticleListDto;
import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.service.ArticleService;
import com.example.webproject_maru.service.Map_r_tService;

@RestController
@RequestMapping("/api/articles")
public class ArticleApiController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private Map_r_tService map_r_tService;

    // 게시글 목록 조회 (검색어가 있는 경우 검색 결과 반환)
    @GetMapping("/list")
    public ResponseEntity<List<ArticleListDto>> getArticles(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "search", required = false) String searchQuery) {

        Pageable pageable = PageRequest.of(page, 30);
        Page<Article> articleEntityPage;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            // 검색어가 있는 경우 검색 결과를 가져옴
            articleEntityPage = articleService.searchArticles(searchQuery, pageable);
        } else {
            // 검색어가 없는 경우 최신순으로 가져옴
            articleEntityPage = articleService.findArticlesDesc(pageable);
        }

        // 각 Article의 태그 리스트를 추가하여 DTO로 변환
        List<ArticleListDto> articleListDtos = articleEntityPage.stream()
            .map(article -> {
                List<String> usedTags = map_r_tService.getOnlyTagsByArticleId(article.getId());
                return ArticleListDto.createArticleListDto(article, usedTags);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return ResponseEntity.ok(articleListDtos);
    }

    //top50 3개월간 점수 높은 작품(점수순)
    @GetMapping("/top")
    public ResponseEntity<List<ArticleListDto>> getArticles(@AuthenticationPrincipal CustomUserDetails userDetails) {

       //1. 50개 데이터 가져오기 list<entity>
       List<Article> highArticleEntityList=articleService.get3mRecentHighScoreArticles(50);

        List<ArticleListDto> articleListDtos = new ArrayList<>();
       //모든 데이터 가져오기 list<entity>

       // 각 Article의 태그 리스트 가져오기
       for (Article article : highArticleEntityList) {
            List<String> usedTags=map_r_tService.getOnlyTagsByArticleId(article.getId());//태그
            ArticleListDto articleListDto=ArticleListDto.createArticleListDto(article,usedTags);
            if (articleListDto != null) {
                articleListDtos.add(articleListDto);
            }
       }

        return ResponseEntity.ok(articleListDtos);
    }


}
