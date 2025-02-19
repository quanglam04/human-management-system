package com.vti.lab7.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vti.lab7.config.CustomUserDetails;
import com.vti.lab7.config.jwt.JwtTokenProvider;
import com.vti.lab7.constant.ErrorMessage;
import com.vti.lab7.dto.request.LoginRequestDto;
import com.vti.lab7.dto.response.LoginResponseDto;
import com.vti.lab7.model.User;
import com.vti.lab7.repository.RoleRepository;
import com.vti.lab7.repository.UserRepository;
import com.vti.lab7.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final MessageSource messageSource;

	public void init() {
		if (userRepository.count() == 0) {
			User user = new User();
			user.setUsername("hiep");
			user.setPassword(passwordEncoder.encode("1234"));
			user.setRole(roleRepository.findByRoleName("ADMIN"));
			userRepository.save(user);
		}
	}

	public LoginResponseDto login(LoginRequestDto request) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

			String accessToken = jwtTokenProvider.generateToken(customUserDetails, Boolean.FALSE);
			String refreshToken = jwtTokenProvider.generateToken(customUserDetails, Boolean.TRUE);

			System.out.println(accessToken + " " + refreshToken);

			return new LoginResponseDto(accessToken, refreshToken);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(ErrorMessage.ERR_EXCEPTION_GENERAL);
		}
	}

}
