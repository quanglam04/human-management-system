package com.vti.lab7.dto.response;

import lombok.Getter;

@Getter
public class TokenRefreshResponseDto {

	private final String accessToken;

	private final String refreshToken;

	public TokenRefreshResponseDto(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

}