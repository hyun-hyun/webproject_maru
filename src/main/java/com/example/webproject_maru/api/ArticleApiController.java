package com.example.webproject_maru.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    //게시글 목록
    @GetMapping("/")
    public ResponseEntity<List<ArticleListDto>> getArticles(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                        @RequestParam("page") int page) {

        //페이지네이션 적용(단위 : 30개)
        Pageable pageable=PageRequest.of(page,30);
        List<Article> articleEntityList=articleService.findArticlesDesc(pageable);

        List<ArticleListDto> articleListDtos = new ArrayList<>();
       //모든 데이터 가져오기 list<entity>

       // 각 Article의 태그 리스트 가져오기
       for (Article article : articleEntityList) {
            List<String> usedTags=map_r_tService.getOnlyTagsByArticleId(article.getId());//태그
            ArticleListDto articleListDto=ArticleListDto.createArticleListDto(article,usedTags);
            if (articleListDto != null) {
                articleListDtos.add(articleListDto);
            }
       }

        return ResponseEntity.ok(articleListDtos);
    }
}
