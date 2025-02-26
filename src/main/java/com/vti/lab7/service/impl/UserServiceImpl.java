package com.vti.lab7.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vti.lab7.config.CustomUserDetails;
import com.vti.lab7.config.jwt.JwtTokenProvider;
import com.vti.lab7.constant.ErrorMessage;
import com.vti.lab7.constant.RoleConstants;
import com.vti.lab7.dto.request.LoginRequestDto;
import com.vti.lab7.dto.request.NewUserRequest;
import com.vti.lab7.dto.request.TokenRefreshRequestDto;
import com.vti.lab7.dto.request.UpdateUserRequest;
import com.vti.lab7.dto.request.UserRequest;
import com.vti.lab7.dto.response.LoginResponseDto;
import com.vti.lab7.dto.response.TokenRefreshResponseDto;
import com.vti.lab7.dto.response.UserDTO;
import com.vti.lab7.dto.response.UserResponse;
import com.vti.lab7.exception.custom.BadRequestException;
import com.vti.lab7.exception.custom.NotFoundException;
import com.vti.lab7.model.Department;
import com.vti.lab7.model.Employee;
import com.vti.lab7.model.Role;
import com.vti.lab7.model.User;
import com.vti.lab7.repository.EmployeeRepository;
import com.vti.lab7.repository.PositionRepository;
import com.vti.lab7.repository.RoleRepository;
import com.vti.lab7.repository.UserRepository;
import com.vti.lab7.service.JwtTokenService;
import com.vti.lab7.service.UserService;
import com.vti.lab7.specification.UserSpecification;
import com.vti.lab7.util.JwtUtil;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

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
	private final JwtTokenService jwtTokenService;
	
	public void init() {
		if (userRepository.count() == 0) {
			// Tạo user Admin
            User adminUser = new User();
            adminUser.setUsername("hiep");
            adminUser.setPassword(passwordEncoder.encode("1234"));
            adminUser.setEmail("hiep@example.com");
            adminUser.setRole(roleRepository.findByRoleName(RoleConstants.ADMIN)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy vai trò ADMIN")));
            userRepository.save(adminUser);

            // Tạo user Manager
            User managerUser = new User();
            managerUser.setUsername("hiep2");
            managerUser.setPassword(passwordEncoder.encode("1234"));
            managerUser.setEmail("hiep2@example.com");
            managerUser.setRole(roleRepository.findByRoleName(RoleConstants.MANAGER)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy vai trò MANAGER")));
            userRepository.save(managerUser);

            // Tạo user Employee
            Role employeeRole = roleRepository.findByRoleName(RoleConstants.EMPLOYEE)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy vai trò EMPLOYEE"));

            List<User> users = IntStream.rangeClosed(1, 18).mapToObj(i -> {
                User user = new User();
                user.setUsername("employee" + i);
                user.setPassword(passwordEncoder.encode("1234"));
                user.setEmail("employee" + i + "@example.com");
                user.setRole(employeeRole);
                return user;
            }).toList();

            userRepository.saveAll(users);
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
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai tài khoản hoặc mật khẩu");
	    } catch (Exception e) {
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống, thử lại sau");
	    }
	}
	
	@Override
	public void logout(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication
	) {
		String accessToken = JwtUtil.extractTokenFromRequest(request);
		String refreshToken = JwtUtil.extractRefreshTokenFromRequest(request);

		if (accessToken != null) {
			// Lưu accessToken vào blacklist
			jwtTokenService.blacklistAccessToken(accessToken);
		}

		if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken) && jwtTokenProvider.isRefreshToken(refreshToken)) {
			// Lưu refreshToken vào blacklist
			jwtTokenService.blacklistRefreshToken(refreshToken);
		}

		SecurityContextLogoutHandler logout = new SecurityContextLogoutHandler();
		logout.logout(request, response, authentication);
	}
	 
	@Override
	public TokenRefreshResponseDto refresh(TokenRefreshRequestDto request) {
		String refreshToken = request.getRefreshToken();

		if (jwtTokenProvider.validateToken(refreshToken) && jwtTokenProvider.isRefreshToken(refreshToken)) {
			Long userId = jwtTokenProvider.extractSubjectFromJwt(refreshToken);
			if (userId != null && jwtTokenService.isTokenAllowed(refreshToken)) {
				User user = userRepository.findById(userId)
						.orElseThrow(() -> new BadRequestException(ErrorMessage.User.ERR_INVALID_REFRESH_TOKEN));
				CustomUserDetails userDetails = new CustomUserDetails(user.getUserId(), user.getUsername(), user.getPassword(),
						user.getRole().getRoleName(),CustomUserDetailsServiceImpl. mapToGrantedAuthorities(user.getRole().getRolePermissions()));

				String newAccessToken = jwtTokenProvider.generateToken(userDetails, Boolean.FALSE);
				String newRefreshToken = jwtTokenProvider.generateToken(userDetails, Boolean.TRUE);

				return new TokenRefreshResponseDto(newAccessToken, newRefreshToken);
			}
		}

		throw new BadRequestException(ErrorMessage.User.ERR_INVALID_REFRESH_TOKEN);
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
