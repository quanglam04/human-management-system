package com.vti.lab7.service;

import com.vti.lab7.dto.request.LoginRequestDto;
import com.vti.lab7.dto.response.LoginResponseDto;

public interface UserService {
	public LoginResponseDto login(LoginRequestDto request);

	public void init();
}
