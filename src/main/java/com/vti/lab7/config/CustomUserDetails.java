package com.vti.lab7.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

	@Getter
	private final long userId;

	@JsonIgnore
	private final String username;

	@JsonIgnore
	private final String password;

	@Getter
	private final String roleName;

	private final Collection<? extends GrantedAuthority> authorities;

	public CustomUserDetails(long userId, String username, String password, String roleName,
			Collection<? extends GrantedAuthority> authorities) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.roleName = roleName;
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

}
