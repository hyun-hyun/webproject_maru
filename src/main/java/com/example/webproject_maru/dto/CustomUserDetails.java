package com.example.webproject_maru.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.webproject_maru.entity.Member;

public class CustomUserDetails implements UserDetails {
    public final Member member;
    public CustomUserDetails(Member member) {

        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return member.getRole();
            }
        });

        return collection;
    }

    public Member getMember() {
        return member;
    }

    public String getNickname(){
        return member.getNickname();
    }

    public Long getId(){
        return member.getId();
    }

    @Override
    public String getPassword() {
        return member.getPswd();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
