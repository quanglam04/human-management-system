package com.vti.lab7;

import org.springframework.core.env.Environment;

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
import lombok.extern.log4j.Log4j2;

@Log4j2
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
		Environment env = SpringApplication.run(Lab7Application.class, args).getEnvironment();
		String appName = env.getProperty("spring.application.name");
		if (appName != null) {
			appName = appName.toUpperCase();
		}
		String port = env.getProperty("server.port");
		log.info("-------------------------START {} Application------------------------------", appName);
		log.info("   Application         : {}", appName);
		log.info("   Url swagger-ui      : http://localhost:{}/swagger-ui.html", port);
		log.info("-------------------------START SUCCESS {} Application----------------------", appName);
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
