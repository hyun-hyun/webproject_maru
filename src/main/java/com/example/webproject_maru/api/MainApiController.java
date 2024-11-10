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

import com.example.webproject_maru.dto.ArticleMainDto;
import com.example.webproject_maru.dto.ArticleRecommendDto;
import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.entity.Map_r_t;
import com.example.webproject_maru.service.ArticleService;
import com.example.webproject_maru.service.Map_r_tService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/main")
public class MainApiController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private Map_r_tService map_r_tService;

    //1개월간 점수 높은 작품(점수순)
    @GetMapping("/top")
    public ResponseEntity<List<ArticleMainDto>> getMainTopArticles(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ArticleMainDto> articleMainDtos = new ArrayList<>();
       //1. 15개 데이터 가져오기 list<entity>
       List<Article> highArticleEntityList=articleService.getRecentHighScoreArticles(15);
       // 각 Article의 태그 리스트 가져오기
       for (Article article : highArticleEntityList) {
            List<String> usedTags=map_r_tService.getOnlyTagsByArticleId(article.getId());//태그
            ArticleMainDto articleMainDto=ArticleMainDto.createArticleMainDto(article,usedTags);
            if (articleMainDto != null) {
                articleMainDtos.add(articleMainDto);
            }
       }

        return ResponseEntity.ok(articleMainDtos);
    }

    //최근순
    @GetMapping("/recent")
    public ResponseEntity<List<ArticleMainDto>> getMainRecentArticles(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ArticleMainDto> articleMainDtos = new ArrayList<>();
       //1. 15개 데이터 가져오기 list<entity>
        List<Article> articleEntityList=articleService.findLimitArticlesDesc(15);

       // 각 Article의 태그 리스트 가져오기
       for (Article article : articleEntityList) {
            List<String> usedTags=map_r_tService.getOnlyTagsByArticleId(article.getId());//태그
            ArticleMainDto articleMainDto=ArticleMainDto.createArticleMainDto(article,usedTags);
            if (articleMainDto != null) {
                articleMainDtos.add(articleMainDto);
            }
       }

        return ResponseEntity.ok(articleMainDtos);
    }

}
