package com.example.UserService.service;

import com.example.UserService.api.status.ErrorStatus;
import com.example.UserService.dto.token.TokenResponse;
import com.example.UserService.dto.user.UserFeignResponse;
import com.example.UserService.dto.user.UserInfoResponse;
import com.example.UserService.dto.user.UserRequest;
import com.example.UserService.dto.user.UserResponse;
import com.example.UserService.entity.RefreshToken;
import com.example.UserService.exception.GeneralException;
import com.example.UserService.entity.User;
import com.example.UserService.jwt.TokenProvider;
import com.example.UserService.repository.RefreshTokenRepository;
import com.example.UserService.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final TokenService tokenService;
    private final TokenProvider tokenProvider;

    public void confirmId(String userId) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isPresent()) {
            throw new GeneralException(ErrorStatus._ALREADY_JOINED_USER);
        }
    }

//    public UserInfoResponse getUser(HttpServletRequest request){
//        UserFeignResponse response = getUserIdByToken(request);
//        User user = userRepository.findByUserId(response.getUserId())
//                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
//        return new UserInfoResponse(user);
//    }

    public UserInfoResponse getUserInfo(String userId){
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

        return new UserInfoResponse(user);
    }

    public void postUpdateUser(String userId, UserRequest.UpdateRequest request){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

        try {
            String newPassword = request.getUserPassword();
            if(newPassword != null) user.setUserPassword(encoder.encode(newPassword));

            String newPhoneNumber = request.getUserPhoneNumber();
            if(newPhoneNumber != null) user.setUserPhoneNumber(newPhoneNumber);

            userRepository.save(user);
        } catch (Exception e) {
            //log.error("Unexpected error occurred: {}", e.getMessage());
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
    }

    public void join(UserRequest.JoinRequest request) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        try {
            User user = User.builder()
                    .userId(request.getUserId())
                    .userName(request.getUserName())
                    .userPassword(encoder.encode(request.getUserPassword()))
                    .userEmail(request.getUserEmail())
                    .userPhoneNumber(request.getUserPhoneNumber())
                    .build();

            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            //log.error("Data integrity violation: {}", e.getMessage());
            throw new GeneralException(ErrorStatus._ALREADY_JOINED_USER);
        } catch (Exception e) {
            //log.error("Unexpected error occurred: {}", e.getMessage());
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
    }

    public UserResponse login(UserRequest.LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

        boolean passwordMatches = encoder.matches(request.getUserPassword(), user.getUserPassword());
        if (passwordMatches) {
            // 토큰 발급
            TokenResponse token = tokenService.getAuthToken(user.getUserId(), request.getUserPassword());

            // 쿠키 설정
            saveCookie(response, "accessToken", token.getAccessToken());

            return new UserResponse(user.getUserId(), token.getAccessToken());
        } else {
            throw new GeneralException(ErrorStatus._INVALID_PASSWORD);
        }
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        // 쿠키 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    public static void saveCookie(HttpServletResponse response, String name, String value) {
        // 쿠키에 accessToken 저장
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("http://localhost:3000");// 특정 도메인에서 사용하도록
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 30); // 30분
        response.addCookie(cookie);
    }

    public void refresh(HttpServletResponse response, String accessToken) {
        try {
            if (accessToken != null) {
                TokenResponse newToken = tokenService.getRefresh(accessToken);

                // 쿠키 AccessToken 업데이트
                saveCookie(response, "accessToken", newToken.getAccessToken());
            } else {
                throw new GeneralException(ErrorStatus._TOKEN_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
    }

    public UserFeignResponse getUserIdByToken(HttpServletRequest request){
        // 쿠키에서 토큰 추출
        Optional<Cookie> accessToken = getCookie((HttpServletRequest) request,"accessToken" );
        String token = String.valueOf(accessToken.get().getValue());
        // RedisRepository에서 객체 추출
        RefreshToken refreshToken = tokenService.findById(token);

        return new UserFeignResponse(refreshToken.getUserId());
    }
}

//    public AccessTokenResponse getAccessToken(String organizationName, String refreshToken) {
//        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
//        Long nowTime = new Date().getTime();
//
//        // AccessToken 발급
//        String newAccessToken = tokenProvider.createAccessToken(authentication, nowTime);
//
//        return new AccessTokenResponse(newAccessToken);
//    }