package com.vti.lab7.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {

	private Long permissionId;

	@NotBlank(message = "Permission name must not be empty.")
	@Size(max = 50, message = "Permission name must not exceed 50 characters.")
	private String permissionName;

	@NotBlank(message = "Description must not be empty.")
	@Size(max = 255, message = "Description must not exceed 255 characters.")
	private String description;

}
