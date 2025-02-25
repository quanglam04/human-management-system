package com.vti.lab7;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.vti.lab7.service.EmployeeService;
import com.vti.lab7.service.IDeparmentService;
import com.vti.lab7.service.PermissionService;
import com.vti.lab7.service.PositionService;
import com.vti.lab7.service.RolePermissionService;
import com.vti.lab7.service.RoleService;
import com.vti.lab7.service.UserService;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class Lab7Application {

	private final RoleService roleService;
	private final PermissionService permissionService;
	private final RolePermissionService rolePermissionService;
	private final UserService userService;
	private final IDeparmentService deparmentService;
	private final PositionService positionService;
	private final EmployeeService employeeService;

	public static void main(String[] args) {
		SpringApplication.run(Lab7Application.class, args);
	}

	@Bean
	CommandLineRunner init() {
		return args -> {
			roleService.init();
			permissionService.init();
			rolePermissionService.init();
			deparmentService.init();
			positionService.init();
			userService.init();
			employeeService.init();
		};
	}
}
