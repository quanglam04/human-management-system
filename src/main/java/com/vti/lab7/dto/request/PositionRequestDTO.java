package com.vti.lab7.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PositionRequestDTO {
	@NotBlank(message = "Name should not be blank")
	private String positionName;
}
