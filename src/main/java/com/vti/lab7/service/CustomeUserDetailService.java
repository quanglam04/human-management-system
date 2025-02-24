package com.vti.lab7.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomeUserDetailService extends UserDetailsService {
	public UserDetails loadUserByUserId(long userId);
}
