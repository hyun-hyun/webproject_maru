package com.example.webproject_maru.controller;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.service.LoginService;


@Controller
public class MainController {
    

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




        return "main";
    }

    @GetMapping("/user/mypage")
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String nickname = userDetails.member.getNickname();
        model.addAttribute("nickname", nickname);
        return "user/mypage";
    }

    @GetMapping("/admin/member")
    public String adminMember(){
        return "admins/adminMember";
    }
}
