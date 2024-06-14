package com.example.UserService.dto.user;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequest {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class JoinRequest{ // 회원가입
        private String userId;
        private String userName;
        private String userPassword;
        private String userEmail;
        private String userPhoneNumber;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginRequest{
        private String userId;
        private String userPassword;
    }

    @Getter
    public static class UpdateRequest{
        private String userPassword;
        private String userPhoneNumber;
    }
}
