package com.example.webproject_maru.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberEditForm {
    private Long id;
    private String nickname="";
    private String gender;
}
