package com.vti.lab7.dto.request;

import com.vti.lab7.constant.ErrorMessage;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
	@NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
	@Size(max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @Pattern(regexp = "^[a-z0-9._-]+$", message = "Username chỉ chứa chữ in thường, số, dấu gạch dưới, gạch ngang, dấu chấm")
    private String username;
	
	@Email(message = ErrorMessage.INVALID_FORMAT_EMAIL)
    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
	@Size(max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String email;
	
	@NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
	@Size(max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$", message = "Password phải có ít nhất 1 chữ cái viết hoa và 1 số")
    private String password;
	
	@NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
	@Pattern(
		    regexp = "EMPLOYER|MANAGER|ADMIN",
		    message = "Role phải là 1 trong số EMPLOYER, MANAGER, or ADMIN"
		)
	private String role;
}
