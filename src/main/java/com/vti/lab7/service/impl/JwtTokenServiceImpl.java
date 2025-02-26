package com.vti.lab7.service.impl;

import com.vti.lab7.config.jwt.JwtTokenProvider;
import com.vti.lab7.service.JwtTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtTokenServiceImpl implements JwtTokenService {

	@Value("${jwt.access.expiration_time:60}")
	int expirationTimeAccessToken;

	@Value("${jwt.refresh.expiration_time:1440}")
	int expirationTimeRefreshToken;

	final RedisTemplate<String, Object> redisTemplate;

	final JwtTokenProvider jwtTokenProvider;

	@Override
	public void blacklistAccessToken(String accessToken) {
		redisTemplate.opsForValue().set(accessToken, "blacklisted", jwtTokenProvider.getRemainingTime(accessToken),
				TimeUnit.MILLISECONDS);
	}

	@Override
	public void blacklistRefreshToken(String refreshToken) {
		redisTemplate.opsForValue().set(refreshToken, "blacklisted", jwtTokenProvider.getRemainingTime(refreshToken),
				TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean isTokenAllowed(String token) {
		return Boolean.FALSE.equals(redisTemplate.hasKey(token));
	}
}