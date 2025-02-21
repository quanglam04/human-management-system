package com.vti.lab7.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vti.lab7.dto.request.LoginRequestDto;
import com.vti.lab7.dto.response.LoginResponseDto;
import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/login")
	public ResponseEntity<RestData<?>> login(@Valid @RequestBody LoginRequestDto request) {
		LoginResponseDto responseDto = userService.login(request);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

}
