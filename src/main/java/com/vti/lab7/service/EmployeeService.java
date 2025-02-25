package com.vti.lab7.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.vti.lab7.config.CustomUserDetails;
import com.vti.lab7.dto.EmployeeDTO;
import com.vti.lab7.dto.response.PaginationResponseDto;

public interface EmployeeService {
	
	void init();

	PaginationResponseDto<EmployeeDTO> getAllEmployees(String firstName, String lastName, String phoneNumber,
			String status, Pageable pageable, CustomUserDetails currentUser);

	EmployeeDTO getEmployeeById(Long id, CustomUserDetails currentUser);

	EmployeeDTO createEmployee(EmployeeDTO employee, CustomUserDetails currentUser);

	EmployeeDTO updateEmployee(Long id, EmployeeDTO employee, CustomUserDetails currentUser);

	void deleteEmployee(Long id);

	List<EmployeeDTO> getEmployeesByDepartment(Long departmentId);

	List<EmployeeDTO> getEmployeesByPosition(Long positionId);

	EmployeeDTO getEmployeeByUserId(Long userId);
	

}
