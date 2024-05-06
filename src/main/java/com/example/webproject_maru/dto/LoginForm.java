package com.example.webproject_maru.dto;

import com.example.webproject_maru.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginForm {
    private Long id;
    private String email;
    private String pswd;
    private String loginDate;


}
