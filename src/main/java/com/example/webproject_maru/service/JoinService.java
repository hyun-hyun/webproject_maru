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

    Boolean isNick;
    Boolean isEmail;
    public void joinProcess(MemberForm memberForm){

        Member data=new Member();

        data.setNickname(memberForm.getNickname());
        data.setEmail(memberForm.getEmail());
        data.setPswd(bCryptPasswordEncoder.encode(memberForm.getPswd()));
        data.setGender(memberForm.getGender());

        LocalDateTime SeoulNow = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        data.setAppendDate(SeoulNow);
        data.setUpdateDate(SeoulNow);

        //data.setRole("ROLE_ADMIN");
        data.setRole("ROLE_USER");

        memberRepository.save(data);
    }

    public boolean getIsNick(String nickname) {
        return memberRepository.existsByNickname(nickname).size() > 0;
    }

    public boolean getIsEmail(String email) {
        return memberRepository.existsByEmail(email).size() > 0;
    }


    public Boolean getIsNick(MemberForm memberForm){
        if(memberRepository.existsByNickname(memberForm.getNickname()).size()==0){
            isNick=false;
            log.info("getttttttttt의 isNick=0");

        }
        else{
            isNick=true;
            log.info("getttttttttt의 isNick=1");

        }
        return isNick;
    }

    public Boolean getIsEmail(MemberForm memberForm){
        if(memberRepository.existsByEmail(memberForm.getEmail()).size()==0){
            isEmail=false;
        }
        else{
            isEmail=true;
        }
        return isEmail;
    }
}
