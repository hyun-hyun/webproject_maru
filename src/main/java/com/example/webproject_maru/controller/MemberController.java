package com.example.webproject_maru.controller;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.dto.LoginForm;
import com.example.webproject_maru.dto.MemberForm;
import com.example.webproject_maru.repository.MemberRepository;
import com.example.webproject_maru.service.JoinService;
import com.example.webproject_maru.service.LoginService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberController {
    //@Autowired
    //private MemberRepository memberRepository;

    @Autowired
    private JoinService joinService;
    //private LoginService loginService;

    @Autowired
    private LoginService loginService;

    @GetMapping ("/join")
    public String goJoin(){
        return "members/join";
    }

    @PostMapping("/joinProc")
    public String joinProcess(MemberForm memberForm, Model model) {
        log.info(memberForm.getNickname());

        // 클라이언트에서 이미 중복 검사를 수행하므로 서버에서 검사하지 않음
        // 만약 클라이언트에서 오류 메시지를 서버에 전달
        Boolean nicknameError = joinService.getIsNick(memberForm.getNickname());
        Boolean emailError = joinService.getIsEmail(memberForm.getEmail());

        // 중복 확인 후 오류 메시지를 추가
        if (nicknameError) {
            model.addAttribute("nicknameError", true);
        }

        if (emailError) {
            model.addAttribute("emailError", true);
        }

        // 중복이 없으면 회원가입 진행
        if (!nicknameError && !emailError) {
            joinService.joinProcess(memberForm);
            return "members/login";
        } else {
            // 중복이 있을 경우 다시 회원가입 페이지로 리턴
            return "members/join";
        }
    }

    // 닉네임 중복 확인
    @GetMapping("/checkNickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        boolean exists = joinService.getIsNick(nickname);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    // 이메일 중복 확인
    @GetMapping("/checkEmail")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = joinService.getIsEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/login")
    public String goLogin(@RequestParam(name = "error", required = false)String error, Model model){
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        // else{
        //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //     UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //     String username = userDetails.getUsername();
        // }
        
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