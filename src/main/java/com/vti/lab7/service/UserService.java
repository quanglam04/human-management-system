package com.vti.lab7.service;

import com.vti.lab7.dto.request.LoginRequestDto;
import com.vti.lab7.dto.request.UserRequest;
import com.vti.lab7.dto.response.LoginResponseDto;
import com.vti.lab7.dto.response.UserResponse;

import org.springframework.data.domain.Page;

public interface UserService {
	Page<UserResponse> getUsers(UserRequest request);
	public LoginResponseDto login(LoginRequestDto request);
	public void init();
}
