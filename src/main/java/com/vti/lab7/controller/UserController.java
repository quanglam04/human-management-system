package com.vti.lab7.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vti.lab7.dto.request.LoginRequestDto;
import com.vti.lab7.dto.request.NewUserRequest;
import com.vti.lab7.dto.request.UserRequest;
import com.vti.lab7.dto.response.UserResponse;
import com.vti.lab7.dto.response.LoginResponseDto;
import com.vti.lab7.dto.response.RestData;
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
	public ResponseEntity<RestData<?>> login(@Valid @RequestBody LoginRequestDto request) {
		LoginResponseDto responseDto = userService.login(request);
		RestData<?> restData = new RestData<>(200, null, "Login success", responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@GetMapping
	@PreAuthorize("hasAuthority('get_all_users')")
	public ResponseEntity<RestData<?>> getUsers(
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
        userRequest.setSortBy(sortBy);
        userRequest.setSortDirection(sortDirection);
        RestData<?> restData = new RestData<>(200, null, "Danh sach toan bo users", userService.getUsers(userRequest));
        return ResponseEntity.ok().body(restData);
    }
	
	@GetMapping("/{userId}")
	@PreAuthorize("hasAuthority('get_user_by_id')")
	public ResponseEntity<RestData<?>> getUserById(
			@PathVariable long userId
			) {
		UserRequest userRequest = new UserRequest();
		userRequest.setUserId(userId);
		RestData<?> restData = new RestData<>(200, null, "Thong tin cua user id: " + String.valueOf(userId), userService.getUserById(userRequest));
		return ResponseEntity.ok().body(restData);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('create_user_any_role')")
	public ResponseEntity<RestData<?>> postNewUser(
			@Valid @RequestBody NewUserRequest userRequest
			) {
		RestData<?> restData = new RestData<>(200, null, "User "+userRequest.getUsername()+" duoc tao thanh cong" , userService.postNewUser(userRequest));
		return ResponseEntity.ok().body(restData);
	}
	
//	@PutMapping("/{userId}")
//	@PreAuthorize("hasAuthority('get_user_by_id')")
//	public ResponseEntity<RestData<?>> getUserById(
//			@PathVariable long userId
//			) {
//		UserRequest userRequest = new UserRequest();
//		userRequest.setUserId(userId);
//		RestData<?> restData = new RestData<>(200, null, "Thong tin cua user id: " + String.valueOf(userId), userService.getUserById(userRequest));
//		return ResponseEntity.ok().body(restData);
//	}
}
