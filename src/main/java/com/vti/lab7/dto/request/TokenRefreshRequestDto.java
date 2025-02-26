package com.vti.lab7.dto.request;

import com.vti.lab7.constant.ErrorMessage;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequestDto {

	@NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
	private String refreshToken;

}