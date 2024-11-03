package com.example.webproject_maru.api;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import com.example.webproject_maru.service.EmailService;
import com.example.webproject_maru.service.JoinService;

//import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api")
public class MemberApiController {
    @Autowired
    private JoinService joinService;
    //@Autowired
    //private EmailService emailService;
    //@Autowired
    //private StringRedisTemplate redisTemplate;

    // 닉네임 중복 확인
    @GetMapping("/checkNickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        boolean exists = joinService.getIsNick(nickname);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    // 이메일 중복 확인
    @GetMapping("/checkEmail")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = joinService.getIsEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
/* 
    //이메일 인증
    //private final Map<String, String> verificationCodes = new HashMap<>();

    @PostMapping("/send-verification")
    public ResponseEntity<String> sendVerification(@RequestParam String email) throws MessagingException {
        String code = generateVerificationCode();
        redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES); // 5분 후 만료
        //verificationCodes.put(email, code);
        try{
            emailService.sendVerificationEmail(email, code);
        }catch(Exception e){
            System.out.println("email send Error: "+e);
        }
        return ResponseEntity.ok("인증번호 메일 전송 완료");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Boolean> verifyCode(@RequestParam String email, @RequestParam String code) {
        //String correctCode = verificationCodes.get(email);
        String correctCode = redisTemplate.opsForValue().get(email);

        if (correctCode != null && correctCode.equals(code)) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private SecureRandom random = new SecureRandom();

    private String generateVerificationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }
    */
}
