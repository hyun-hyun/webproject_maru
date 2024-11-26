package com.example.webproject_maru.api;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.webproject_maru.dto.CustomUserDetails;
import com.example.webproject_maru.entity.Member;
//import com.example.webproject_maru.service.EmailService;
import com.example.webproject_maru.service.JoinService;
import com.example.webproject_maru.service.MemberService;

//import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api")
public class MemberApiController {
    @Autowired
    private JoinService joinService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private MemberService memberService;
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

    @PostMapping("/user/deleteAccount")
    public ResponseEntity<Map<String, Object>> deleteAccount(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> request) {
        String password = request.get("password");

        // 비밀번호 확인 (입력된 비밀번호와 저장된 비밀번호가 일치하는지 확인)
        Member member = userDetails.member;
        if (!bCryptPasswordEncoder.matches(password, member.getPswd())) {
            // 비밀번호가 일치하지 않으면 오류 반환
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            return ResponseEntity.ok(response);
        }

        try {
            // 비밀번호가 일치하면 회원 탈퇴 처리
            memberService.deleteMemberAccount(member.getId());  // 나머지 정보 삭제 (ID와 nickname 제외)
            
            // 세션 종료
            SecurityContextHolder.clearContext();

            // 탈퇴 성공 응답
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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
