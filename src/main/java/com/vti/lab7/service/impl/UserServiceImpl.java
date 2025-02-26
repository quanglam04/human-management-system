package com.vti.lab7.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vti.lab7.config.CustomUserDetails;
import com.vti.lab7.config.jwt.JwtTokenProvider;
import com.vti.lab7.constant.ErrorMessage;
import com.vti.lab7.constant.RoleConstants;
import com.vti.lab7.dto.EmployeeDTO;
import com.vti.lab7.dto.request.LoginRequestDto;
import com.vti.lab7.dto.request.NewUserRequest;
import com.vti.lab7.dto.request.UpdateUserRequest;
import com.vti.lab7.dto.request.UserRequest;
import com.vti.lab7.dto.response.LoginResponseDto;
import com.vti.lab7.dto.response.PaginationResponseDto;
import com.vti.lab7.dto.response.UserDTO;
import com.vti.lab7.dto.response.UserResponse;
import com.vti.lab7.exception.custom.ForbiddenException;
import com.vti.lab7.exception.custom.NotFoundException;
import com.vti.lab7.model.Department;
import com.vti.lab7.model.Employee;
import com.vti.lab7.model.Position;
import com.vti.lab7.model.Role;
import com.vti.lab7.model.User;
import com.vti.lab7.repository.EmployeeRepository;
import com.vti.lab7.repository.PositionRepository;
import com.vti.lab7.repository.RoleRepository;
import com.vti.lab7.repository.UserRepository;
import com.vti.lab7.service.EmployeeService;
import com.vti.lab7.service.UserService;
import com.vti.lab7.specification.UserSpecification;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static com.vti.lab7.constant.RoleConstants.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final EmployeeRepository employeeRepository;
	private final PositionRepository positionRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	public void init() {
		if (userRepository.count() == 0) {
			User user = new User();
			user.setUsername("hiep");
			user.setPassword(passwordEncoder.encode("1234"));
			user.setEmail("hiep@example.com");
			user.setRole(roleRepository.findByRoleName("ADMIN").orElseThrow(() -> new EntityNotFoundException("Khong tim thay role")));
			userRepository.save(user);
			User user2 = new User();
			user2.setUsername("hiep2");
			user2.setPassword(passwordEncoder.encode("1234"));
			user2.setRole(roleRepository.findByRoleName("MANAGER").orElseThrow(() -> new EntityNotFoundException("Khong tim thay role")));
			userRepository.save(user2);
			User user3 = new User();
			user3.setUsername("hiep3");
			user3.setPassword(passwordEncoder.encode("1234"));
			user3.setRole(roleRepository.findByRoleName("EMPLOYEE").orElseThrow(() -> new EntityNotFoundException("Khong tim thay role")));
			userRepository.save(user3);
			User user4 = new User();
			user4.setUsername("hiep4");
			user4.setPassword(passwordEncoder.encode("1234"));
			user4.setRole(roleRepository.findByRoleName("EMPLOYEE").orElseThrow(() -> new EntityNotFoundException("Khong tim thay role")));
			userRepository.save(user4);
		}
	}

	public User getCurrentUser() {
		String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByUsername(currentUsername)
				.orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_USERNAME, currentUsername));
	}
	
	public LoginResponseDto login(LoginRequestDto request) {
	    try {
	        Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

	        String accessToken = jwtTokenProvider.generateToken(customUserDetails, Boolean.FALSE);
	        String refreshToken = jwtTokenProvider.generateToken(customUserDetails, Boolean.TRUE);

	        return new LoginResponseDto(accessToken, refreshToken);
	    } catch (BadCredentialsException e) {
	        throw new BadCredentialsException("");
	    } catch (Exception e) {
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống, thử lại sau");
	    }
	}

	@Override
	public Page<UserDTO> getUsers(UserRequest request) {
		Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy())
        );
		
		Specification<User> spec = Specification
				.where(UserSpecification.hasUsername(request.getUsername()))
				.and(UserSpecification.hasEmail(request.getEmail()));
        Page<User> users = userRepository.findAll(spec, pageable);

        return users.map(user -> new UserDTO(user));
	}
	@Override
	public Page<UserDTO> getUsersOfDepartment(UserRequest request, long departmentId) {
		Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy())
        );

		Specification<User> spec = Specification
				.where(UserSpecification.hasUsername(request.getUsername()))
				.and(UserSpecification.hasEmail(request.getEmail()))
				.and(UserSpecification.belongsToDepartment(departmentId));
		Page<User> users = userRepository.findAll(spec, pageable);

        return users.map(user -> new UserDTO(user));
	}

	@Override
	public UserResponse getUserById(UserRequest request) {
		User user = userRepository.findByUserId(request.getUserId()).orElse(null);
		return user != null ? new UserResponse(user) : null;
	}

	@Override
	public UserResponse postNewUser(NewUserRequest request) {
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEmail(request.getEmail());
		user.setRole(roleRepository.findByRoleName(request.getRole()).orElseThrow(() -> new EntityNotFoundException("Khong tim thay role")));
		userRepository.save(user);
		
		return new UserResponse(user);
	}
	@Override
	@Transactional
	public UserResponse postNewUserEmployer(@Valid NewUserRequest request, Department department) {
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEmail(request.getEmail());
		user.setRole(roleRepository.findByRoleName(EMPLOYEE).orElseThrow(() -> new EntityNotFoundException("Khong tim thay role")));
		
		Employee em = new Employee();
		em.setUser(user);
		em.setDepartment(department);
		em.setPosition(positionRepository.findByPositionName("developer").orElseThrow(() -> new EntityNotFoundException("Không tồn tại position")));
		
		user.setEmployee(em);
		
		userRepository.save(user);
		employeeRepository.save(em);
		return new UserResponse(user);
	}
	
	@Override
	public User getUserClassById(long userId) {
		return userRepository.findByUserId(userId).orElse(null);
	}

	@Override
	public User getUserClassByUserName(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}

	@Override
	public UserDTO updateUser(@Valid UpdateUserRequest userRequest, User user) {

	    if (userRequest.getEmail() != null && !userRequest.getEmail().isBlank()) {
	        user.setEmail(userRequest.getEmail());
	    }
	    if (userRequest.getRole() != null && !userRequest.getRole().isBlank()) {
	        Role newRole = roleRepository.findByRoleName(userRequest.getRole())
	        		.orElseThrow(() -> new EntityNotFoundException("Khong tim thay role"));
	                
	        user.setRole(newRole);
	    }

	    
	    userRepository.save(user);
	    
		return new UserDTO(user);
	}

	@Override
	public UserDTO updateUserDepartment(@Valid UpdateUserRequest userRequest, User user, Department department) {
	    if (userRequest.getEmail() != null && !userRequest.getEmail().isBlank()) {
	        user.setEmail(userRequest.getEmail());
	    }
	    if (userRequest.getRole() != null && !userRequest.getRole().isBlank()) {
	        Role newRole = roleRepository.findByRoleName(userRequest.getRole())
	        		.orElseThrow(() -> new EntityNotFoundException("Khong tim thay role"));
	                
	        user.setRole(newRole);
	    }
	    
	    userRepository.save(user);
	    
		return new UserDTO(user);
	}

	@Override
	public void deleteUser(long userId) {
		if (!userRepository.existsById(userId)) {
            throw new InvalidDataAccessResourceUsageException("Không tìm thấy user");
        }
        userRepository.deleteById(userId);
	}

	

}
