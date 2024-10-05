package com.example.webproject_maru.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.entity.Article;
import com.example.webproject_maru.service.ArticleService;
import com.example.webproject_maru.service.LoginService;
import com.example.webproject_maru.service.Map_a_tService;
import com.example.webproject_maru.service.Map_r_tService;


@Controller
public class MainController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private Map_r_tService map_r_tService;

    @GetMapping("/")
    public String goMain(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        model.addAttribute("id", email);
        model.addAttribute("role", role);

        if(!email.equals("anonymousUser")){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String nickname = userDetails.getNickname();
            model.addAttribute("nickname", nickname);

            if(role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER")){
                model.addAttribute("write", 1);
            }

            if(role.equals("ROLE_ADMIN")){
                model.addAttribute("admin",1);
            }
        }

       // String nickname=LoginService.getNickname(email);

       //3개월간 점수 높은 작품(점수순)
       //1. 10개 데이터 가져오기 list<entity>
       List<Article> highArticleEntityList=articleService.getRecentHighScoreArticles(15);
       // 각 Article의 태그 리스트 가져오기
       for (Article article : highArticleEntityList) {
           Long articleId = article.getId();
           List<String> usedTags = map_r_tService.getOnlyTagsByArticleId(articleId);  // List<String>으로 태그를 가져옴
           article.setUsedTags(usedTags);  // Article 엔티티에 태그 리스트를 추가
       }
       //2. 모델에 데이터 등록
       model.addAttribute("highArticleList", highArticleEntityList);


        //등록한 게시물 내림차순(최신등록작품)
        //1. 모든 데이터 가져오기 list<entity>
        List<Article> articleEntityList=articleService.findLimitArticlesDesc(15);
        // 각 Article의 태그 리스트 가져오기
        for (Article article : articleEntityList) {
            Long articleId = article.getId();
            List<String> usedTags = map_r_tService.getOnlyTagsByArticleId(articleId);  // List<String>으로 태그를 가져옴
            article.setUsedTags(usedTags);  // Article 엔티티에 태그 리스트를 추가
        }
        //2. 모델에 데이터 등록
        model.addAttribute("articleList", articleEntityList);


        return "main";
    }

    @GetMapping("/user/mypage")
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String nickname = userDetails.member.getNickname();
        Long member_id = userDetails.member.getId();

        model.addAttribute("nickname", nickname);
        model.addAttribute("member_id", member_id);
        return "user/mypage";
    }

    @GetMapping("/admin/member")
    public String adminMember(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        String nickname = userDetails.member.getNickname();
        model.addAttribute("nickname", nickname);
        return "admin/adminMember";
    }
}
