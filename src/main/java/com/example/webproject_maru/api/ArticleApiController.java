package com.example.webproject_maru.api;

import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.webproject_maru.dto.ArticleForm;
import com.example.webproject_maru.dto.ArticleListDto;
import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.service.ArticleService;
import com.example.webproject_maru.service.CommentService;
import com.example.webproject_maru.service.LoveService;
import com.example.webproject_maru.service.Map_r_tService;
import com.example.webproject_maru.service.ReviewService;

@RestController
public class ArticleApiController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private Map_r_tService map_r_tService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LoveService loveService;

    // 게시글 목록 조회 (검색어가 있는 경우 검색 결과 반환)
    @GetMapping("/api/articles/list")
    public ResponseEntity<List<ArticleListDto>> getArticles(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "search", required = false) String searchQuery,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "direction", defaultValue = "desc") String direction) {

        Sort sortOption = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, 30,sortOption);
        Page<Article> articleEntityPage;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            // 검색어가 있는 경우 검색 결과를 가져옴
            articleEntityPage = articleService.searchArticles(searchQuery, pageable);
        } else {
            // 검색어가 없는 경우 최신순으로 가져옴
            articleEntityPage = articleService.findArticles(pageable);
            //articleEntityPage = articleService.findArticlesDesc(pageable);
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
    @GetMapping("/api/articles/top")
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

    //게시글 삭제
    @PostMapping("/write/article/{category}/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String category, @PathVariable Long id, ArticleForm form,
                        @RequestParam(required = false) boolean admin, RedirectAttributes redirectAttributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        if(form.getId()!=id){
             return ResponseEntity.badRequest().body(Map.of("error", "해당 작품에 id정보 불일치가 발생했습니다."));
        }
        
        //manager권한에서는 리뷰가 있을 때는 작품삭제 불가
        boolean existingReview = reviewService.existsByArticleId(id);
        boolean existingComment = commentService.existsByArticleId(id);
        if(!admin && existingReview && existingComment){

            return ResponseEntity.badRequest().body(Map.of("error", "해당 작품에 리뷰나 댓글이 있어 삭제가 불가합니다. 관리자권한으로 시도해주세요."));

        }
        //관리자권한일 경우 삭제 혹은 리뷰연관 없을경우 삭제(권한 더블체크)
        if((admin && role.equals("ROLE_ADMIN")) || (!existingReview&&(role.equals("ROLE_MANAGER")&&!existingComment))){
                
            //태그, 리뷰, 사진, 기본 삭제
            articleService.delete(form, category, existingReview, existingComment);
            return ResponseEntity.ok(Map.of("message", "삭제가 완료되었습니다."));
        }
        
        
        return ResponseEntity.badRequest().body(Map.of("error", "삭제할 수 없습니다."));
    }

    //게시글 찜
    @PostMapping("/user/{articleId}/love")
    @ResponseBody
    public Map<String, Object> toggleLove(@PathVariable Long articleId,@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getId();
        boolean isLoved = loveService.toggleLove(memberId, articleId);

        // JSON 형식으로 응답 반환
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isLoved", isLoved);
        return response;
    }
}
