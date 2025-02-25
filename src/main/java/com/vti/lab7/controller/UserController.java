package com.vti.lab7.controller;

import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
import com.vti.lab7.dto.request.UpdateUserRequest;
import com.vti.lab7.dto.request.UserRequest;
import com.vti.lab7.dto.response.UserResponse;
import com.vti.lab7.model.Department;
import com.vti.lab7.model.User;
import com.vti.lab7.dto.response.LoginResponseDto;
import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.dto.response.UserDTO;
import com.vti.lab7.service.UserService;
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

	@GetMapping
	@PreAuthorize("hasAuthority('get_all_users') or hasAuthority('get_department_users')")
	public ResponseEntity<RestData<?>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Authentication authentication) {

        UserRequest userRequest = new UserRequest();
        userRequest.setPage(page);
        userRequest.setSize(size);
        userRequest.setUsername(username);
        userRequest.setEmail(email);
        userRequest.setSortBy(sortBy);
        userRequest.setSortDirection(sortDirection);
        RestData<?> restData = null;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("get_all_users"))) {
    		restData = new RestData<>(200, null, "Danh sach toan bo users", userService.getUsers(userRequest));
    	}
        else if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("get_department_users"))){
    		long userId = ((CustomUserDetails)authentication.getPrincipal()).getUserId();
    		System.out.println(userId);
    		User user = userService.getUserClassById(userId);
    		long departmentId = (user.getEmployee()) != null ? 
    				user.getEmployee().getDepartment().getDepartmentId()
    				: -1;
    		if(departmentId == -1) {
    			restData = new RestData<>(404, "Not Found" , ErrorMessage.ERR_RESOURCE_NOT_FOUND, null);
    			return ResponseEntity.status(404).body(restData);
    		}
    		else restData = new RestData<>(200, null, "Danh sach users", userService.getUsersOfDepartment(userRequest, departmentId));
    	}
        
        
        return ResponseEntity.ok().body(restData);
    }
	
	@GetMapping("/{userId}")
	@PreAuthorize("hasAuthority('get_user_by_id') or hasAuthority('get_department_user_by_id') or hasAuthority('get_own_info')")
	public ResponseEntity<RestData<?>> getUserById(
			@PathVariable long userId,
			Authentication authentication
			) {
		UserRequest userRequest = new UserRequest();
		userRequest.setUserId(userId);
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		UserResponse userResponse = null;
		if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("get_user_by_id"))) {
			userResponse = userService.getUserById(userRequest);
		} 
		else if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("get_department_user_by_id"))) {
			User user = userService.getUserClassById(userId);
			User userCurrent = userService.getUserClassById(((CustomUserDetails)authentication.getPrincipal()).getUserId());
			if(user.getEmployee() == null || userCurrent.getEmployee() == null) userResponse = null;
			else {
				if (user.getEmployee().getDepartment().getDepartmentId()
						== userCurrent.getEmployee().getDepartment().getDepartmentId()) {
					userResponse = userService.getUserById(userRequest);
				}
			}
		}
		else if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("get_own_info"))) {
			long userCurrentId = ((CustomUserDetails)authentication.getPrincipal()).getUserId();
			if(userId != userCurrentId) userResponse = null;
			else userResponse = userService.getUserById(userRequest);
		}
//		userResponse.setEmployee(employeeService.getEmployeeByUserId(userId));
		RestData<?> restData = new RestData<>(404, "Not Found", ErrorMessage.ERR_RESOURCE_NOT_FOUND, null);
		if(userResponse != null)
			restData = new RestData<>(200, null, "Thong tin cua user id: " + String.valueOf(userId), userResponse);
		else {
			return ResponseEntity.status(404).body(restData);
		}
		return ResponseEntity.ok().body(restData);
	}
	@PostMapping
	@PreAuthorize("hasAuthority('create_user_any_role') or hasAuthority('create_employee_in_department')")
	public ResponseEntity<RestData<?>> postNewUser(
			@Valid @RequestBody NewUserRequest userRequest,
			Authentication authentication
			) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		UserResponse userResponse = null;
		RestData<?> restData = null;
		if(userService.getUserClassByUserName(userRequest.getUsername()) != null) {
			restData = new RestData<>(400, "Bad Request", "Đã tồn tại username này", null);
			return ResponseEntity.status(400).body(restData);
		}
		
		if(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("create_user_any_role"))) {
			userResponse = userService.postNewUser(userRequest);
		} 
		else if(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("create_employee_in_department"))) {
			long userId = ((CustomUserDetails)authentication.getPrincipal()).getUserId();
			User user = userService.getUserClassById(userId);
			Department department = user.getEmployee() != null ? user.getEmployee().getDepartment() : null;
			if(department == null) {
				restData = new RestData<>(400, "Bad Request", "Không tìm thấy department của manager", null);
				return ResponseEntity.status(400).body(restData);
			}
			else userResponse = userService.postNewUserEmployer(userRequest, department);
		}
		restData = new RestData<>(400, "Bad Request", "Tạo user mới thất bại", null);
		if(userResponse != null) restData = new RestData<>(200, null, "User "+userRequest.getUsername()+" duoc tao thanh cong" , userResponse);
		return ResponseEntity.ok().body(restData);
	}
	
	@PutMapping("/{userId}")
	@PreAuthorize("hasAuthority('update_any_user') or hasAuthority('update_department_user') or hasAuthority('update_own_info')")
	public ResponseEntity<RestData<?>> putUserById(
			@PathVariable long userId,
			@Valid @RequestBody UpdateUserRequest userRequest,
			Authentication authentication
			) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		UserDTO userResponse = null;
		RestData<?> restData = null;
		long userCurrentId = ((CustomUserDetails)authentication.getPrincipal()).getUserId();
		
		if(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("update_any_user"))) {
			userResponse = userService.updateUser(userRequest, userId);
		}
		else if(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("update_department_user"))) {
			User userCurrent = userService.getUserClassById(userCurrentId);
			Department department = userCurrent.getEmployee() != null ? userCurrent.getEmployee().getDepartment() : null;
			if(department == null) {
				restData = new RestData<>(400, "Bad Request", "Không tìm thấy department của manager", null);
				return ResponseEntity.status(400).body(restData);
			}
			
			userResponse = userService.updateUserDepartment(userRequest, userId, department);
			
		}
		else if(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("update_own_info"))) {
			if(userId != userCurrentId) {
				restData = new RestData<>(400, "Bad Request", "Bạn không có quyền cập nhật user khác", null);
				return ResponseEntity.status(400).body(restData);
			}
			userRequest.setRole(null);
			userResponse = userService.updateUser(userRequest, userId);
		}
		restData = new RestData<>(400, "Bad Request", "Cập nhật user id: " + userId + " thất bại", null);
		if(userResponse != null) restData = new RestData<>(200, null, "User id: "+ userId +" cập nhật thành công" , userResponse);
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
