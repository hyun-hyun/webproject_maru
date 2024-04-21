package com.example.webproject_maru.controller;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

       // String nickname=LoginService.getNickname(email);

        model.addAttribute("id", email);
        model.addAttribute("role", role);

        return "main";
    }

    @GetMapping("/admin/member")
    public String adminMember(){
        return "admins/adminMember";
    }
}
