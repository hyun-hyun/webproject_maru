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
import com.example.webproject_maru.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MainController {
    @Autowired
    private MemberService memberService;

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
            Long member_id=userDetails.getId();
            model.addAttribute("nickname", nickname);
            model.addAttribute("member_id", member_id);


            if(role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER")){
                model.addAttribute("write", 1);
            }

            if(role.equals("ROLE_ADMIN")){
                model.addAttribute("admin",1);
            }
        }

       // String nickname=LoginService.getNickname(email);

        return "main";
    }

    @GetMapping("/user/mypage")
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String nickname = userDetails.member.getNickname();
        Long member_id = userDetails.member.getId();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String gender=memberService.findGenderById(member_id);
        String genderDescription;
        switch(gender){
            case "여":genderDescription="여성";
                break;
            case "남":genderDescription="남성";
                break;
            default: genderDescription="비공개";
                break;

        }
        //사용자별 리뷰개수 카운트

        model.addAttribute("nickname", nickname);
        model.addAttribute("member_id", member_id);
        model.addAttribute("email", email);
        model.addAttribute("genderD", genderDescription);
        //사용자별 리뷰개수 카운트 모델등록

        return "user/mypage";
    }
    @GetMapping("/user/mypage/edit")
    public String myEdit(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String nickname = userDetails.member.getNickname();
        Long member_id = userDetails.member.getId();
        String gender=memberService.findGenderById(member_id);

        model.addAttribute("nickname", nickname);
        model.addAttribute("member_id", member_id);
        model.addAttribute("gender", gender);
        return "user/myedit";
    }
    
    @GetMapping("/user/mypage/myreview")
    public String myReview(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String nickname = userDetails.member.getNickname();
        Long member_id = userDetails.member.getId();

        model.addAttribute("nickname", nickname);
        model.addAttribute("member_id", member_id);
        return "user/myreview";
    }

    @GetMapping("/admin/member")
    public String adminMember(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        String nickname = userDetails.member.getNickname();
        model.addAttribute("nickname", nickname);
        return "admin/adminMember";
    }
}
