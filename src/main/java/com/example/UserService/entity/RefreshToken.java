package com.example.UserService.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 21600) // 60 * 60 * 12
public class RefreshToken {
    @Id
    private String userId;

    @Indexed
    private String accessToken;

    private String refreshToken;

    public RefreshToken(String accessToken, String refreshToken, String userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

    public void updateAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
    }
}