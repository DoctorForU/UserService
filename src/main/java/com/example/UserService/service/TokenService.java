package com.example.UserService.service;

import com.example.UserService.api.status.ErrorStatus;
import com.example.UserService.dto.token.AccessTokenResponse;
import com.example.UserService.dto.token.TokenResponse;
import com.example.UserService.entity.RefreshToken;
import com.example.UserService.entity.User;
import com.example.UserService.exception.GeneralException;
import com.example.UserService.jwt.TokenProvider;
import com.example.UserService.repository.RefreshTokenRepository;
import com.example.UserService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;

    public TokenResponse getAuthToken(String userId, String password) {
        try {
            logger.info("Authenticating user with ID: {} and password: {}", userId, password);

            // DB 조회
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
            logger.info("User found: {}", user);

            // 권한 생성 (아이디, 비밀번호)
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);
            logger.info("Authentication token created: {}", authenticationToken);

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            logger.info("Authentication successful for user ID: {}", userId);

            Long createdAt = new Date().getTime();

            // Token 발급
            String accessToken = tokenProvider.createAccessToken(authentication, createdAt);
            logger.info("Access token generated: {}", accessToken);

            String refreshToken = tokenProvider.createRefreshToken(authentication, createdAt);
            logger.info("Refresh token generated: {}", refreshToken);

            // RefreshToken 저장
            RefreshToken redis = new RefreshToken(accessToken, refreshToken, userId);
            refreshTokenRepository.save(redis);
            logger.info("Refresh token saved for user ID: {}", userId);

            return new TokenResponse(accessToken, refreshToken);
        } catch (GeneralException e) {
            logger.error("Error in getAuthToken for user ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while generating auth token for user ID: {}", userId, e);
            throw new GeneralException(ErrorStatus._UNKNOWN); // 예외를 다시 던져서 트랜잭션 롤백이 일어나도록 함
        }
    }

    public TokenResponse getRefresh(String accessToken) {
        try{
            // redis 엔티티 조회
            RefreshToken refreshToken = findById(accessToken);

            // refreshToken -> 권한 추출
            Authentication authentication = tokenProvider.getAuthentication(refreshToken.getRefreshToken());
            Long nowTime = new Date().getTime();

            // AccessToken 재발급
            String newAccessToken = tokenProvider.createAccessToken(authentication, nowTime);

            // redis AccessToken 업데이트
            refreshToken.updateAccessToken(newAccessToken);
            refreshTokenRepository.save(refreshToken);

            return new TokenResponse(accessToken, refreshToken.getRefreshToken());
        } catch (Exception e) {
            //log.error("Unexpected error occurred: {}", e.getMessage());
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
    }

    public RefreshToken findById(String accessToken){
        return refreshTokenRepository.findById(accessToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TOKEN_NOT_FOUND));
    }
}
