package com.example.webproject_maru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.webproject_maru.dto.LoginForm;
import com.example.webproject_maru.dto.MemberForm;
import com.example.webproject_maru.repository.MemberRepository;
import com.example.webproject_maru.service.JoinService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberController {
    //@Autowired
    //private MemberRepository memberRepository;
    
    @Autowired
    private JoinService joinService;

    @GetMapping ("/join")
    public String goJoin(){
        return "members/join";
    }

    @PostMapping("/joinProc")
    public String joinProcess(MemberForm memberForm){
        //log.info(memberForm.getNickname());
        joinService.joinProcess(memberForm);

        return "members/joined";
    }

    @GetMapping("/login")
    public String goLogin(){
        return "members/login";
    }

    @PostMapping("/loginProc")
    public String loginProcess(LoginForm loginForm){
        System.out.println(loginForm.getEmail());
        //joinService.joinProcess(loginForm);

        return "redirect:/";
    }
}