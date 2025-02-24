package com.vti.lab7.dto.request;

import com.vti.lab7.constant.ErrorMessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

	@NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
	@Size(max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
	private String username;

	@NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
	@Size(max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
	private String password;

}
