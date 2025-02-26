package com.vti.lab7.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	private final String API_KEY = "Bearer Token";

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("HR Management API").version("1.0")
						.description("API documentation for the HR Management System with authentication and RBAC"))
				.components(new Components().addSecuritySchemes(API_KEY,
						new SecurityScheme().name("Authorization").scheme("Bearer").bearerFormat("JWT")
								.type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER)))
				.addSecurityItem(new SecurityRequirement().addList(API_KEY));
	}
}
