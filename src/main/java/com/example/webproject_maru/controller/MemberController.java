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
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.dto.LoginForm;
import com.example.webproject_maru.dto.MemberEditForm;
import com.example.webproject_maru.dto.MemberForm;
import com.example.webproject_maru.dto.PasswordEditForm;
import com.example.webproject_maru.repository.MemberRepository;
import com.example.webproject_maru.service.JoinService;
import com.example.webproject_maru.service.LoginService;
import com.example.webproject_maru.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private MemberService memberService;

    @GetMapping ("/join")
    public String goJoin(){
        return "members/join";
    }

    @PostMapping("/joinProc")
    public String joinProcess(MemberForm memberForm) {
        log.info(memberForm.getNickname());

        // 클라이언트에서 이미 중복 검사를 수행함. 서버에서 2차검증
        // 만약 클라이언트에서 오류 메시지를 서버에 전달
        Boolean nicknameError = joinService.getIsNick(memberForm.getNickname());
        Boolean emailError = joinService.getIsEmail(memberForm.getEmail());

        // 중복 확인 후 오류 메시지를 추가
        // if (nicknameError) {
        //     model.addAttribute("nicknameError", true);
        // }

        // if (emailError) {
        //     model.addAttribute("emailError", true);
        // }

        // 중복이 없으면 회원가입 진행
        if (!nicknameError && !emailError) {
            joinService.joinProcess(memberForm);
            return "members/login";
        } else {
            // 중복이 있을 경우 다시 회원가입 페이지로 리턴
            return "members/join";
        }
    }

    
    @GetMapping("/login")
    public String goLogin(@RequestParam(name = "error", required = false)String error, Model model,
                    @RequestParam(name = "successMessage", required = false) String successMessage){
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);  // 비밀번호 변경 성공 메시지 표시
        }
        // else{
        //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //     UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //     String username = userDetails.getUsername();
        // }
        
        return "members/login";
    }

    @PostMapping("/editProc")
    public String editProcess(@AuthenticationPrincipal CustomUserDetails userDetails, MemberEditForm memberEditForm, Model model){
        Long memberId = userDetails.member.getId();
        String gender=memberService.findGenderById(memberId);

        model.addAttribute("member_id", memberId);
        model.addAttribute("gender", gender);

        Boolean nicknameError;
        if(memberEditForm.getNickname()!=null||!memberEditForm.getNickname().equals("")){
            nicknameError = joinService.getIsNick(memberEditForm.getNickname());
        }else nicknameError = false;

        try {
            // 중복이 없으면 회원 수정 진행
            memberService.editProcess(memberId, memberEditForm);
            return "redirect:/user/mypage";  // 수정이 성공하면 마이페이지로 리다이렉트
        } catch (Exception e) {
            model.addAttribute("error", "회원 정보 수정에 실패했습니다: " + e.getMessage());  // 일반적인 에러 메시지
            return "user/myedit";  // 수정 실패 시 수정 페이지로 돌아감
        }
    }

    @PostMapping("/pswdEditProc")
    public String passwordEditProcess(@AuthenticationPrincipal CustomUserDetails userDetails, PasswordEditForm passwordEditForm, Model model
                                    ,HttpServletRequest request, HttpServletResponse response){
        Long memberId = userDetails.member.getId();
        String gender=memberService.findGenderById(memberId);

        model.addAttribute("member_id", memberId);
        model.addAttribute("gender", gender);

        try {
            memberService.passwordEditProcess(memberId, passwordEditForm);

            //비밀번호 변경 성공시 로그아웃 처리
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                // 로그아웃 처리
                new SecurityContextLogoutHandler().logout(request, response, authentication);
            }

            // 리다이렉트: 비밀번호 수정 후 / 페이지로 이동
            return "redirect:/login?successMessage=passwordUpdated";  // 성공 후 메시지 전달
        } catch (IllegalArgumentException e) {
            model.addAttribute("p_error", e.getMessage());  // 에러 메시지를 모델에 추가
            return "user/myedit";  // 에러 발생시 수정 페이지로 다시 돌아가기
        }

    }
/* loginProcessingUrl과 2에서 작성한 action 태그 값만 같으면 스프링 시큐리티가 알아서 처리
    @PostMapping("/loginProc")
    public String loginProcess(LoginForm loginForm){
        System.out.println(loginForm.getEmail());
        //loginService.loginProcess(loginForm.getEmail());

        return "redirect:/";
    }*/

}