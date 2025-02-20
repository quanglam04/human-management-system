package com.vti.lab7.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
	private long employeeId;

	@NotBlank(message = "First name is required")
	@Size(max = 50, message = "First name must not exceed 50 characters")
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(max = 50, message = "Last name must not exceed 50 characters")
	private String lastName;

	@NotNull(message = "Date of birth is required")
	private Date dateOfBirth;

	@NotBlank(message = "Phone number must be 10 digits")
	private String phoneNumber;

	@Size(max = 255, message = "Address must not exceed 255 characters")
	private String address;

	@NotNull(message = "Hire date is required")
	private Date hireDate;

	@Positive(message = "Salary must be positive")
	private BigDecimal salary;

	@NotBlank(message = "Status is required")
	@Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either ACTIVE or INACTIVE")
	private String status;

	@NotNull(message = "User ID is required")
	private Long userId;

	@NotNull(message = "Position ID is required")
	private Long positionId;

	@NotNull(message = "Department ID is required")
	private Long departmentId;
}
