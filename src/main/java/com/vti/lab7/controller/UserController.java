package com.vti.lab7.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vti.lab7.config.CustomUserDetails;
import com.vti.lab7.constant.ErrorMessage;
import com.vti.lab7.dto.request.LoginRequestDto;
import com.vti.lab7.dto.request.NewUserRequest;
import com.vti.lab7.dto.request.TokenRefreshRequestDto;
import com.vti.lab7.dto.request.UpdateUserRequest;
import com.vti.lab7.dto.request.UserRequest;
import com.vti.lab7.dto.response.UserResponse;
import com.vti.lab7.exception.custom.ForbiddenException;
import com.vti.lab7.exception.custom.NotFoundException;
import com.vti.lab7.model.Department;
import com.vti.lab7.model.User;
import com.vti.lab7.dto.response.LoginResponseDto;
import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.dto.response.TokenRefreshResponseDto;
import com.vti.lab7.dto.response.UserDTO;
import com.vti.lab7.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


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
	
	@Operation(summary = "API Logout")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        userService.logout(request, response, authentication);
        
		RestData<?> restData = new RestData<>(200, null, "Logout success", true);
		return ResponseEntity.ok().body(restData);
    }

    @Operation(summary = "API Refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(@Valid @RequestBody TokenRefreshRequestDto tokenRefreshRequestDto) {
    	TokenRefreshResponseDto responseDto = userService.refresh(tokenRefreshRequestDto);
    	
    	RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
    }
	
	@GetMapping("/me")
	public ResponseEntity<RestData<?>> getMe(Authentication authentication) {
		long userId = ((CustomUserDetails)authentication.getPrincipal()).getUserId();
		UserRequest userRequest = new UserRequest();
		userRequest.setUserId(userId);
		UserResponse userResponse = userService.getUserById(userRequest);
		RestData<?> restData = new RestData<>(200, null, "Thông tin của bạn", userResponse);
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
            @RequestParam(defaultValue = "desc") String sortDirection,
            Authentication authentication) {
		
		long userId = ((CustomUserDetails)authentication.getPrincipal()).getUserId();
		String role = ((CustomUserDetails)authentication.getPrincipal()).getRoleName();

        UserRequest userRequest = new UserRequest();
        userRequest.setPage(page);
        userRequest.setSize(size);
        userRequest.setUsername(username);
        userRequest.setEmail(email);
        userRequest.setSortBy(sortBy);
        userRequest.setSortDirection(sortDirection);
        RestData<?> restData = null;
		
		switch(role) {
			case "ADMIN":
				restData = new RestData<>(200, null, "Danh sach toan bo users", userService.getUsers(userRequest));
				break;
			case "MANAGER":
				User user = userService.getUserClassById(userId);
				long departmentId = (user.getEmployee()) != null 
						? user.getEmployee().getDepartment().getDepartmentId()
	    				: -1;
	    		if(departmentId == -1) {
	    			restData = new RestData<>(200, null, "Bạn chưa là manager của department nào", null);
	    			return ResponseEntity.ok().body(restData);
	    		}
	    		else restData = new RestData<>(200, null, "Danh sach users của department " + user.getEmployee().getDepartment().getDepartmentName(), userService.getUsersOfDepartment(userRequest, departmentId));
	    		
	    		break;
			}
        return ResponseEntity.ok().body(restData);
    }
	
	@GetMapping("/{userId}")
	@PreAuthorize("hasAuthority('get_user_by_id')")
	public ResponseEntity<RestData<?>> getUserById(
			@PathVariable long userId,
			Authentication authentication
			) {
		UserRequest userRequest = new UserRequest();
		userRequest.setUserId(userId);
		UserResponse userResponse = null;
		
		long userCurrentId = ((CustomUserDetails)authentication.getPrincipal()).getUserId();
		String role = ((CustomUserDetails)authentication.getPrincipal()).getRoleName();
		User user = userService.getUserClassById(userId);
		User userCurrent = userService.getUserClassById(userCurrentId);

		switch(role) {
			case "ADMIN":
				userResponse = userService.getUserById(userRequest);
				break;
				
			case "MANAGER":
				if(user.getEmployee() == null || userCurrent.getEmployee() == null) userResponse = null;
				else {
					if (user.getEmployee().getDepartment().getDepartmentId()
							== userCurrent.getEmployee().getDepartment().getDepartmentId()) {
						userResponse = userService.getUserById(userRequest);
					}
				}
				break;
				
			case "EMPLOYEE":
				if(userId != userCurrentId) userResponse = null;
				else userResponse = userService.getUserById(userRequest);
				break;
				
		}

		RestData<?> restData = new RestData<>(404, "Not Found", ErrorMessage.ERR_RESOURCE_NOT_FOUND, null);
		if(userResponse != null) 
			restData = new RestData<>(200, null, "Thong tin cua user id: " + String.valueOf(userId), userResponse);
		else return ResponseEntity.status(404).body(restData);
		
		return ResponseEntity.ok().body(restData);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('create_user_any_role')")
	public ResponseEntity<RestData<?>> postNewUser(
			@Valid @RequestBody NewUserRequest userRequest,
			Authentication authentication
			) {
		long userCurrentId = ((CustomUserDetails)authentication.getPrincipal()).getUserId();
		String role = ((CustomUserDetails)authentication.getPrincipal()).getRoleName();
		
		UserResponse userResponse = null;
		RestData<?> restData = null;

		if(userService.getUserClassByUserName(userRequest.getUsername()) != null) {
			restData = new RestData<>(400, "Bad Request", "Đã tồn tại username này", null);
			return ResponseEntity.status(400).body(restData);
		}
		
		switch(role) {
			case "ADMIN":
				userResponse = userService.postNewUser(userRequest);
				break;
				
			case "MANAGER":
				User userCurrent = userService.getUserClassById(userCurrentId);
				Department department = userCurrent.getEmployee() != null ? userCurrent.getEmployee().getDepartment() : null;
				if(department == null) {
					restData = new RestData<>(400, "Bad Request", "Bạn chưa là manager của department nào", null);
					return ResponseEntity.status(400).body(restData);
				}
				else userResponse = userService.postNewUserEmployer(userRequest, department);
			
		}
		
		restData = new RestData<>(500, "Có lỗi xảy ra, vui lòng thử lại sau", "Tạo user mới thất bại", null);
		if(userResponse != null) restData = new RestData<>(200, null, "User "+userRequest.getUsername()+" duoc tao thanh cong" , userResponse);
		else return ResponseEntity.status(500).body(restData);
		
		return ResponseEntity.ok().body(restData);
	}
	
	@PutMapping("/{userId}")
	@PreAuthorize("hasAuthority('update_any_user')")
	public ResponseEntity<RestData<?>> putUserById(
			@PathVariable long userId,
			@Valid @RequestBody UpdateUserRequest userRequest,
			Authentication authentication
			) {
		UserDTO userResponse = null;
		RestData<?> restData = null;
		
		long userCurrentId = ((CustomUserDetails)authentication.getPrincipal()).getUserId();
		String role = ((CustomUserDetails)authentication.getPrincipal()).getRoleName();

		User user = new User();
		switch(role) {
			case "ADMIN":
				user = userService.getUserClassById(userId);
				if(user == null) throw new NotFoundException("Không tìm thấy user", null);
				userResponse = userService.updateUser(userRequest, user);
				break;
				
			case "MANAGER":
				User userCurrent = userService.getUserClassById(userCurrentId);
				Department department = userCurrent.getEmployee() != null ? userCurrent.getEmployee().getDepartment() : null;
				if(department == null) {
					restData = new RestData<>(400, "Bad Request", "Bạn chưa là manager của department nào", null);
					return ResponseEntity.status(400).body(restData);
				}
				
				user = userService.getUserClassById(userId);
				if(user == null) throw new NotFoundException("Không tìm thấy user", null);
				if(user.getEmployee() == null || user.getEmployee().getDepartment().getDepartmentId() != department.getDepartmentId()) {
					throw new ForbiddenException("Không thể truy cập vào user này");
				}
				userResponse = userService.updateUserDepartment(userRequest, user, department);
				break;
				
			case "EMPLOYEE":
				if(userId != userCurrentId) {
					throw new ForbiddenException("Bạn không có quyền cập nhật user khác");
				}
				userRequest.setRole(null);
				userResponse = userService.updateUser(userRequest, user);
				break;
		}
		

		restData = new RestData<>(500, "Có lỗi xảy ra, vui lòng thử lại sau", "Cập nhật user id: " + userId + " thất bại", null);
		if(userResponse != null) restData = new RestData<>(200, null, "User id: "+ userId +" cập nhật thành công" , userResponse);
		else return ResponseEntity.status(500).body(restData);
		
		return ResponseEntity.ok().body(restData);
	}
	
	@DeleteMapping("/{userId}")
	@PreAuthorize("hasAuthority('delete_any_user')")
	public ResponseEntity<RestData<?>> deleteUserById (
			@PathVariable long userId
			) {
		userService.deleteUser(userId);
		return ResponseEntity.ok(new RestData<>(200, null, "Xóa user id:" + userId +" thành công", null));
	}
}
