package com.vti.lab7.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vti.lab7.config.CustomUserDetails;
import com.vti.lab7.model.RolePermission;
import com.vti.lab7.model.User;
import com.vti.lab7.repository.UserRepository;
import com.vti.lab7.service.CustomeUserDetailService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsServiceImpl implements CustomeUserDetailService {

	UserRepository userRepository;

	private Collection<? extends GrantedAuthority> mapToGrantedAuthorities(List<RolePermission> rolePermissions) {
		return rolePermissions.stream()
				.map(rolePermission -> new SimpleGrantedAuthority(rolePermission.getPermission().getPermissionName()))
				.collect(Collectors.toSet());
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException(String.format("User with username '%s' not found.", username)));
		return new CustomUserDetails(user.getUserId(), user.getUsername(), user.getPassword(),
				user.getRole().getRoleName(), mapToGrantedAuthorities(user.getRole().getRolePermissions()));
	}

	@Override
	@Transactional
	public UserDetails loadUserByUserId(long userId) throws UsernameNotFoundException {
		User user = userRepository.findById(userId).orElseThrow(
				() -> new UsernameNotFoundException(String.format("User with ID '%d' not found.", userId)));

		return new CustomUserDetails(user.getUserId(), user.getUsername(), user.getPassword(),
				user.getRole().getRoleName(), mapToGrantedAuthorities(user.getRole().getRolePermissions()));
	}

}