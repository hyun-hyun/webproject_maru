package com.example.webproject_maru.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.MemberForm;
import com.example.webproject_maru.entity.Member;
import com.example.webproject_maru.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JoinService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(MemberForm memberForm){

        //DB에 이미 동일한 nickname, email을 가진 회원이 존재하는지?
        Boolean isNick=memberRepository.existsByNickname(memberForm.getNickname());
        Boolean isEmail=memberRepository.existsByEmail(memberForm.getEmail());
        if((isNick!=null)||(isEmail!=null)){
            log.info("중복member 존재");
            return;
        }
        log.info("중복member 존재없음!");


        Member data=new Member();

        data.setNickname(memberForm.getNickname());
        data.setEmail(memberForm.getEmail());
        data.setPswd(bCryptPasswordEncoder.encode(memberForm.getPswd()));



        // LocalDateTime SeoulNow = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        // data.setAppendDate(SeoulNow);
        // data.setUpdateDate(SeoulNow);

        // data.setRole("ROLE_USER");

        memberRepository.save(data);
    }
}
