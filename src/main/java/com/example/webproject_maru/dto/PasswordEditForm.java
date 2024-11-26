package com.example.webproject_maru.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordEditForm {
    private Long id;
    private String pswd;//기존 비밀번호
    private String n_pswd;//신규 비밀번호
}
