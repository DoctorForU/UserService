package com.example.UserService.service;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import com.example.UserService.exception.GeneralException;
import com.example.UserService.api.status.ErrorStatus;
import com.example.UserService.mail.VerificationCode;
import com.example.UserService.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    @Value("${spring.mail.username}")
    private String serviceEmail;
    private static final Integer EXPIRATION_TIME_IN_MINUTES = 5;
    private final SpringTemplateEngine templateEngine;

    private final JavaMailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;

    public void sendVerificationMail(String to, LocalDateTime sentAt) throws MessagingException { // 이메일 인증 코드 전송
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        VerificationCode verificationCode = generateVerificationCode(sentAt); // 인증 코드 생성
        verificationCodeRepository.save(verificationCode); //인증 코드 저장

        HashMap<String, Object> templateModel = new HashMap<>();
        templateModel.put("verificationCode", verificationCode.generateCodeMessage());

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = templateEngine.process("mail.html", thymeleafContext);

        helper.setFrom(serviceEmail);
        helper.setTo(to);
        helper.setSubject("DoctorForU 인증코드");
        helper.setText(htmlBody, true);

        try{
            mailSender.send(message);
        }catch (MailException e) {
            log.error("Error sending email to {}: {}", to, e.getMessage());
        }catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage());
        }
    }

    private VerificationCode generateVerificationCode(LocalDateTime sentAt) { // 인증 코드 발급
        String code = UUID.randomUUID().toString();
        return VerificationCode.builder()
                .code(code)
                .createAt(sentAt)
                .expirationTimeInMinutes(EXPIRATION_TIME_IN_MINUTES)
                .build();
    }

    public void verifyCode(String code, LocalDateTime verifiedAt) { // 인증 코드 검증
        VerificationCode verificationCode = verificationCodeRepository.findByCode(code)
                .orElseThrow(() -> new GeneralException(ErrorStatus._VERIFICATION_CODE_NOT_FOUND));

        if (verificationCode.isExpired(verifiedAt)) {
            throw new GeneralException(ErrorStatus._VERIFICATION_CODE_EXPIRED);
        }

        verificationCodeRepository.remove(verificationCode);
    }

}