package com.vti.lab7.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vti.lab7.config.CustomUserDetails;
import com.vti.lab7.model.User;
import com.vti.lab7.repository.UserRepository;
import com.vti.lab7.service.CustomeUserDetailService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsServiceImpl implements UserDetailsService, CustomeUserDetailService {

	UserRepository userRepository;


	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException(""));

		return new CustomUserDetails(user.getUserId(), user.getUsername(), user.getPassword(),
				user.getRole().getRolePermissions());
	}

	@Transactional
	public UserDetails loadUserByUserId(long userId) throws UsernameNotFoundException {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(""));

		return new CustomUserDetails(user.getUserId(), user.getUsername(), user.getPassword(),
				user.getRole().getRolePermissions());
	}

}