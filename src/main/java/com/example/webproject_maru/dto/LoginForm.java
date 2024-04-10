package com.example.webproject_maru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginForm {
    private Long id;
    private String pswd;
    private String email;
    private String updateDate;
}
