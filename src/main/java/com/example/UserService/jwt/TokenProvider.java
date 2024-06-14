package com.example.UserService.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private Key key;
    // 어세스 토큰 유효시간 (1시간)
    private long accessTokenValidTime = 1 * 60 * 60 * 1000L;
    // 리프레시 토큰 유효시간 (1일)
    private long refreshTokenValidTime = 24 * 60 * 60 * 1000L;

    @Autowired
    public TokenProvider(@Value("${jwt.secret}") String secret) {
        this.secret = secret;

        // 시크릿 값을 복호화(decode) 하여 키 변수에 할당
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access Token 생성.
    public String createAccessToken(Authentication authentication, Long nowTime){
        log.info("Authentication: {} and time {}", authentication, nowTime); //

        Date validity = new Date(nowTime + accessTokenValidTime);
        return this.createToken(authentication, validity);
    }
    // Refresh Token 생성.
    public String createRefreshToken(Authentication authentication, Long nowTime) {
        log.info("Authentication: {} and time {}", authentication, nowTime);
        Date validity = new Date(nowTime + refreshTokenValidTime);
        return this.createToken(authentication, validity);
    }

    public String createToken(Authentication authentication, Date expired) {
        logger.info("createToken with : " + authentication); //

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        logger.info("authorities: " + authorities); //

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512) // HMAC + SHA512
                .setExpiration(expired)
                .compact();
    }

    //토큰을 받아 클레임을 생성
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        //.getPayload();

        List<GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities);
    }

    // 토큰 유효성 체크
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            e.printStackTrace();
        }

        return false;
    }
}