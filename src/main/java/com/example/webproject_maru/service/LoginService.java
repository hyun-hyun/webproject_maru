package com.example.webproject_maru.service;



import java.util.ArrayList;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.dto.LoginForm;
import com.example.webproject_maru.entity.Member;
import com.example.webproject_maru.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginService implements UserDetailsService {
	@Autowired
	private MemberRepository memberRepository;
	
	@Override
	public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Member memberData = memberRepository.findByEmail(email);
		
        if (memberData != null) {

            return new CustomUserDetails(memberData);
        }
		return null;
	}
}
/*
public class LoginService {
    @Autowired
    private MemberRepository memberRepository;

    
    public void loginProcess(LoginForm loginForm){

        
        Member memberData = memberRepository.findByEmail(loginForm.getEmail());
    }

}
*/
