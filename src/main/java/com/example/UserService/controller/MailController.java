package com.example.UserService.controller;

import com.example.UserService.api.CommonResponse;
import com.example.UserService.api.status.SuccessStatus;
import com.example.UserService.dto.mail.MailRequest;
import com.example.UserService.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/verify-email")
    public CommonResponse<Void> getEmailForVerification(@RequestBody MailRequest.EmailForVerificationRequest request) throws MessagingException {
        LocalDateTime requestedAt = LocalDateTime.now();
        mailService.sendVerificationMail(request.getEmail(), requestedAt);
        return CommonResponse.of(SuccessStatus._ACCEPTED, null);
    }

    @PostMapping("/verification-code")
    public CommonResponse<String> verificationByCode(@RequestBody MailRequest.VerificationCodeRequest request) {
        LocalDateTime requestedAt = LocalDateTime.now();
        mailService.verifyCode(request.getCode(), requestedAt);
        return CommonResponse.ok("정상 인증 완료");
    }

}