package com.example.webproject_maru.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class SubPic {
    @Column
    private String pic;//저장되는uuid이름
    @Column
    private String name;//저장되는 original이름
    @Column
    private String realChar;//원래 캐릭터 이름
    @Column
    private String realVoiceChar;//원래 캐릭터 성우
    @Column
    private String korChar;//한국캐릭터 이름
    @Column
    private String korVoiceChar;//한국 캐릭터 성우


    public SubPic(String realChar, String realVoiceChar, String korChar, String korVoiceChar){
        this.realChar=realChar;
        this.realVoiceChar=realVoiceChar;
        this.korChar=korChar;
        this.korVoiceChar=korVoiceChar;
    }

}
