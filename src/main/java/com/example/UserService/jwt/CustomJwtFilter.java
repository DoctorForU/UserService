package com.example.UserService.jwt;

import com.example.UserService.service.TokenService;
import com.example.UserService.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomJwtFilter implements Filter {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenProvider tokenProvider;
    private TokenService tokenService;
    private UserService userService;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String jwt = resolveToken(req); // 이게 FE에서 날라온 토큰임 (access)
        String requestURI = req.getRequestURI();

        // 회원가입 경로는 인증을 건너뜁니다. - 6월 11일
        if (requestURI.equals("/user-service/register")) {
            chain.doFilter(request, response);
            return;
        }

        // 쿠키에서 토큰 추출
        Optional<Cookie> accessToken = userService.getCookie((HttpServletRequest) request,"accessToken" );

        if (accessToken.isPresent()) {
            String token = String.valueOf(accessToken.get().getValue());
            // 토큰 유효성 검사
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) { // 토큰에 이상이 없는 경우
                // 토큰에서 사용자명, 권한을 추출하여 스프링 시큐리티 사용자를 만들어 Authentication 반환
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context에 %s 인증 정보를 저장했습니다. URI : %s", authentication.getName(), requestURI);
            } else {
                // 토큰이 만료되었을 경우 새로운 토큰 발급
                userService.refresh((HttpServletResponse) response, token);
                //log.debug("유효한 JWT 토큰이 없습니다. URI: %s", requestURI);
            }
        }
        //다음 필터로 넘기기
        chain.doFilter(request, response);
    }

    // HttpServletRequest에서 `Authorization` 헤더를 받음.
    // 헤더에서 'Bearer'로 시작하는 토큰이 있으면 'Bearer' 부분 제거하고 토큰 값 반환 아니면 널 값 반환
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
