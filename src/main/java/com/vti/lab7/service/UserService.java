package com.vti.lab7.service;

import com.vti.lab7.dto.request.LoginRequestDto;
import com.vti.lab7.dto.request.NewUserRequest;
import com.vti.lab7.dto.request.UpdateUserRequest;
import com.vti.lab7.dto.request.UserRequest;
import com.vti.lab7.dto.response.LoginResponseDto;
import com.vti.lab7.dto.response.UserDTO;
import com.vti.lab7.dto.response.UserResponse;
import com.vti.lab7.model.Department;
import com.vti.lab7.model.User;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;

public interface UserService {
	Page<UserDTO> getUsers(UserRequest request);
	Page<UserDTO> getUsersOfDepartment(UserRequest request, long departmentId);
	UserResponse getUserById(UserRequest request);
	User getUserClassById(long userId);
	User getUserClassByUserName(String username);
	UserResponse postNewUser(@Valid NewUserRequest userRequest);
	UserResponse postNewUserEmployer(@Valid NewUserRequest userRequest, Department department);
	UserDTO updateUser(@Valid UpdateUserRequest userRequest, User user);
	UserDTO updateUserDepartment(@Valid UpdateUserRequest userRequest, User user, Department department);
	void deleteUser(long userId);
	public LoginResponseDto login(LoginRequestDto request);

	public void init();
	public User getCurrentUser();
}
