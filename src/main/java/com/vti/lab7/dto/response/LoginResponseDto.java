package com.vti.lab7.dto.response;

import lombok.Getter;

@Getter
public class LoginResponseDto {

	private final String tokenType = "Bearer";

	private final String accessToken;

	private final String refreshToken;

	public LoginResponseDto(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
