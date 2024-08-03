package com.example.webproject_maru.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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


@Controller
public class MainController {
    @Autowired
    private ArticleService articleService;

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

        if(email!="anonymousUser"){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String nickname = userDetails.getNickname();
            model.addAttribute("nickname", nickname);

            if(role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER")){
                model.addAttribute("write", 1);
            }

            if(role=="ROLE_ADMIN"){
                model.addAttribute("admin",1);
            }
        }

       // String nickname=LoginService.getNickname(email);

        //등록한 게시물 내림차순(최신등록작품)
        //1. 모든 데이터 가져오기 list<entity>
        ArrayList<Article> articleEntityList=articleService.findArticlesDesc();
        //2. 모델에 데이터 등록
        model.addAttribute("articleList", articleEntityList);


        return "main";
    }

    @GetMapping("/user/mypage")
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String nickname = userDetails.member.getNickname();
        model.addAttribute("nickname", nickname);
        return "user/mypage";
    }

    @GetMapping("/admin/member")
    public String adminMember(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        String nickname = userDetails.member.getNickname();
        model.addAttribute("nickname", nickname);
        return "admin/adminMember";
    }
}
