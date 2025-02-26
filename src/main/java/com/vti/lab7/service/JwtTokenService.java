package com.vti.lab7.service;

public interface JwtTokenService {
    void blacklistAccessToken(String accessToken);

    void blacklistRefreshToken(String refreshToken);

    boolean isTokenAllowed(String token);
}