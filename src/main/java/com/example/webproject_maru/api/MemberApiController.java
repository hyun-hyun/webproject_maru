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

import jakarta.transaction.Transactional;

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

    //비번찾기(임시비밀번호)
    @Transactional
    @PostMapping("/update-temporary-password")
    public ResponseEntity<String> updateTemporaryPassword(@RequestParam String email, @RequestParam String temporaryPassword) {
        // 이메일로 회원을 조회
        Member member = memberService.findByEmail(email); // 회원 정보 조회

        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보를 찾을 수 없습니다.");
        }

        // 임시 비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(temporaryPassword);  // 임시 비밀번호 암호화

        // 회원의 비밀번호를 임시 비밀번호로 업데이트
        member.setPswd(encodedPassword);  // 비밀번호 설정
        memberService.updateMemberPassword(member);  // 비밀번호 업데이트 서비스 호출

        return ResponseEntity.ok("임시 비밀번호로 변경되었습니다.");
    }

    //계정삭제
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

    // 임시 비밀번호 생성 및 반환
    @PostMapping("/send-temporary-password")
    public ResponseEntity<Object> sendTemporaryPassword(@RequestParam String email) throws MessagingException {
        String temporaryPassword = generateTemporaryPassword(); // 10자리 임시 비밀번호 생성
        try {
            // 이메일로 임시 비밀번호는 보내지 않음
            // emailService.sendTemporaryPasswordEmail(email, temporaryPassword); 
        } catch (Exception e) {
            return ResponseEntity.status(500).body("임시 비밀번호 전송 실패");
        }
        return ResponseEntity.ok().body(new TemporaryPasswordResponse(temporaryPassword)); // 임시 비밀번호 반환
    }

    // 임시 비밀번호 생성 로직
    private String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(TEMPORARY_PASSWORD_LENGTH);
        for (int i = 0; i < TEMPORARY_PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
    // 임시 비밀번호 응답 객체
    public static class TemporaryPasswordResponse {
        private String temporaryPassword;

        public TemporaryPasswordResponse(String temporaryPassword) {
            this.temporaryPassword = temporaryPassword;
        }

        public String getTemporaryPassword() {
            return temporaryPassword;
        }

        public void setTemporaryPassword(String temporaryPassword) {
            this.temporaryPassword = temporaryPassword;
        }
    }

    */
}
