package com.vti.lab7.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vti.lab7.dto.request.LoginRequestDto;
import com.vti.lab7.dto.request.UserRequest;
import com.vti.lab7.dto.response.UserResponse;
import com.vti.lab7.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
		return ResponseEntity.ok().body(userService.login(request));
	}

	@GetMapping
	@PreAuthorize("hasAuthority('get_all_users')")
	public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        UserRequest userRequest = new UserRequest();
        userRequest.setPage(page);
        userRequest.setSize(size);
        userRequest.setUsername(username);
        userRequest.setEmail(email);
        System.out.println(userRequest);
        userRequest.setSortBy(sortBy);
        userRequest.setSortDirection(sortDirection);

        return ResponseEntity.ok(userService.getUsers(userRequest));
    }
}
