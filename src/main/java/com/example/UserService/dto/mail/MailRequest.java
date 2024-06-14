package com.example.UserService.dto.mail;

import lombok.Getter;

public class MailRequest {
    @Getter
    public static class EmailForVerificationRequest {
        private String email;
    }

    @Getter
    public static class VerificationCodeRequest {
        private String code;
    }
}
