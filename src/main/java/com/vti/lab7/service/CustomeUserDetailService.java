package com.vti.lab7.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomeUserDetailService {
	public UserDetails loadUserByUsername(String username);
	public UserDetails loadUserByUserId(long userId);
}
