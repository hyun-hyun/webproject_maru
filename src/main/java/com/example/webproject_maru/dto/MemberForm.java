package com.example.webproject_maru.dto;

import com.example.webproject_maru.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberForm {
    private Long id;
    private String nickname;
    private String pswd;
    private String email;


}



