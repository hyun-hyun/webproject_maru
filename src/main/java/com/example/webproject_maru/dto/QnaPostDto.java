package com.example.webproject_maru.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QnaPostDto {
    private Long id; // 글 ID
    private String title; // 제목
    private String content; // 내용
    private String authorNickname; // 작성자 닉네임
    private LocalDateTime appendTime; // 작성 시간
    private boolean isNotice; // 공지 여부
    private boolean isDeleted; //삭제여부
    private boolean answered; // 답변 여부
    private Long parentId; // 부모 글 ID (답글일 경우)
}
