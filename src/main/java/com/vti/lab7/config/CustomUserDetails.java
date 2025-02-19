package com.vti.lab7.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vti.lab7.model.RolePermission;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

	@Getter
	private final long userId;

	@JsonIgnore
	private final String username;

	@JsonIgnore
	private final String password;

	private final Collection<? extends GrantedAuthority> authorities;

	public CustomUserDetails(long userId, String username, String password, List<RolePermission> rolePermissions) {
		this.userId = userId;
		this.username = username;
		this.password = password;

		List<GrantedAuthority> tempAuthorities = new ArrayList<>();
		for (RolePermission role : rolePermissions) {
			tempAuthorities.add(new SimpleGrantedAuthority(role.getPermission().getPermissionName()));
		}
		this.authorities = tempAuthorities;
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
