package com.vti.lab7.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Past(message = "Date of birth must be in the past")
    private Date dateOfBirth;

    @NotBlank(message = "Phone number must be 10 digits")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotNull(message = "Hire date is required")
    @PastOrPresent(message = "Hire date cannot be in the future")
    private Date hireDate;

    @Positive(message = "Salary must be positive")
    private BigDecimal salary;

    @NotBlank(message = "Status is required")
    private String status;

    private Long userId;

    private Long positionId;

    private Long departmentId;

    @AssertTrue(message = "Hire date must be after date of birth")
    @JsonIgnore
    public boolean isHireDateValid() {
        if (dateOfBirth != null && hireDate != null) {
            return hireDate.toLocalDate().isAfter(dateOfBirth.toLocalDate());
        }
        return true;
    }

}