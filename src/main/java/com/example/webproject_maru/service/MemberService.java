package com.example.webproject_maru.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.dto.MemberEditForm;
import com.example.webproject_maru.dto.PasswordEditForm;
import com.example.webproject_maru.entity.Member;
import com.example.webproject_maru.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void editProcess(Long id, MemberEditForm memberEditForm) {
        if(id!=memberEditForm.getId()) throw new IllegalArgumentException("수정 실패! 회원정보 불일치");
        log.info("회원정보일치");
        Member data=memberRepository.findById(memberEditForm.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원정보 수정 실패!"+
                        "수정자 memberId가 없습니다."));//해당멤버 없으면 에러 메시지 출력
        log.info("멤버 불러옴");

        if(memberEditForm.getNickname()!="" && memberEditForm.getNickname()!=null){
            data.setNickname(memberEditForm.getNickname());
            updateNicknameInSession(memberEditForm.getNickname());
        }
        data.setGender(memberEditForm.getGender());
        log.info("데이ㅓ 셋!!!!!!");

        memberRepository.save(data);
    }

    @Transactional
    public void passwordEditProcess(Long id, PasswordEditForm passwordEditForm){
        if(id!=passwordEditForm.getId()) throw new IllegalArgumentException("수정 실패! 회원정보 불일치");

        Member member=validatePassword(id, passwordEditForm.getPswd());
        member.setPswd(bCryptPasswordEncoder.encode(passwordEditForm.getN_pswd()));
        memberRepository.save(member);
    }

    public Member validatePassword(Long id, String password){
        Member member=memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("비밀번호 수정 실패!"+
                "수정자 memberId가 없습니다."));//해당멤버 없으면 에러 메시지 출력
        if(!bCryptPasswordEncoder.matches(password,member.getPswd())) throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        return member;
    }

    public String findGenderById(Long id) {
        Member member=memberRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("gender찾기실패!"+
        " memberId가 없습니다."));//해당멤버 없으면 에러 메시지 출력
        return member.getGender();
    }
    
    public void updateNicknameInSession(String newNickname) {
        // 현재 인증된 사용자 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 기존 Authentication 객체에서 CustomUserDetails 가져오기
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // 닉네임을 변경한 새로운 Member 객체 생성
        Member updatedMember = customUserDetails.member;
        updatedMember.setNickname(newNickname);

        // 새로운 CustomUserDetails 객체 생성
        CustomUserDetails updatedUserDetails = new CustomUserDetails(updatedMember);

        // 새로운 Authentication 객체 생성
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                updatedUserDetails, authentication.getCredentials(), updatedUserDetails.getAuthorities());

        // SecurityContext에 새로운 Authentication 객체 설정
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
}
