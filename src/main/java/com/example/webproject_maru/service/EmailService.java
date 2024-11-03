package com.example.webproject_maru.service;
/* 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    public void sendVerificationEmail(String recipientEmail, String verificationCode) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        String htmlMsg = "<h3>회원가입 인증 코드</h3><p>인증을 위해 아래의 코드를 입력해주세요:</p><h2>" + verificationCode + "</h2>";
        helper.setText(htmlMsg, true);
        helper.setTo(recipientEmail);
        helper.setSubject("[마루의 애니 추천 곳간] 이메일 인증 코드");
        helper.setFrom("maruauth@gmail.com");

        emailSender.send(message);
    }
}*/
