package com.vti.lab7.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDTO {
	@NotBlank(message = "Description should not be blank")
	private String description;
	
	@NotBlank(message = "Name should not be blank")
	private String roleName;
}
