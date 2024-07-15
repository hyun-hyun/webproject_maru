package com.example.webproject_maru.dto;


import com.example.webproject_maru.entity.SubPic;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@AllArgsConstructor //클래스 모든 필드의 생성자 만듬 this.title=title;
@NoArgsConstructor
@ToString //클래스 모든 필드의 String으로 확인하는 것 오버라이딩
@Getter
@Setter
public class SubPicForm {
    private String realChar=null;
    private String realVoiceChar=null;
    private String korChar=null;
    private String korVoiceChar=null;


    public SubPic toEntity(){

        return new SubPic(realChar, realVoiceChar, korChar, korVoiceChar);
    }


}
