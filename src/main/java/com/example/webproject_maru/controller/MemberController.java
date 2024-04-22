package com.example.webproject_maru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.webproject_maru.dto.LoginForm;
import com.example.webproject_maru.dto.MemberForm;
import com.example.webproject_maru.repository.MemberRepository;
import com.example.webproject_maru.service.JoinService;
import com.example.webproject_maru.service.LoginService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberController {
    //@Autowired
    //private MemberRepository memberRepository;
    
    @Autowired
    private JoinService joinService;
    //private LoginService loginService;

    @GetMapping ("/join")
    public String goJoin(){
        return "members/join";
    }

    @PostMapping("/joinProc")
    public String joinProcess(MemberForm memberForm, Model model){
        log.info(memberForm.getNickname());
        
        Boolean nicknameError=joinService.getIsNick(memberForm);
        Boolean emailError=joinService.getIsEmail(memberForm);

        if(nicknameError==true){
            model.addAttribute("nicknameError", true);
        }

        if(emailError==true){
            model.addAttribute("emailError", true);
        }

        if(nicknameError==false && emailError==false){
            joinService.joinProcess(memberForm);
            return "members/login";
        } 
        else{
            return "members/join";
        }
    }

    @GetMapping("/login")
    public String goLogin(@RequestParam(name = "error", required = false)String error, Model model){
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "members/login";
    }
/* loginProcessingUrl과 2에서 작성한 action 태그 값만 같으면 스프링 시큐리티가 알아서 처리
    @PostMapping("/loginProc")
    public String loginProcess(LoginForm loginForm){
        System.out.println(loginForm.getEmail());
        //loginService.loginProcess(loginForm.getEmail());

        return "redirect:/";
    }*/
}