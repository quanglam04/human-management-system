package com.vti.lab7.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.vti.lab7.dto.EmployeeDTO;
import com.vti.lab7.dto.response.PaginationResponseDto;

public interface EmployeeService {

	PaginationResponseDto<EmployeeDTO> getAllEmployees(String firstName, String lastName, String phoneNumber, String status,
			Pageable pageable);

	EmployeeDTO getEmployeeById(Long id);

	EmployeeDTO createEmployee(EmployeeDTO employee);

	EmployeeDTO updateEmployee(Long id, EmployeeDTO employee);

	void deleteEmployee(Long id);

	List<EmployeeDTO> getEmployeesByDepartment(Long departmentId);

	List<EmployeeDTO> getEmployeesByPosition(Long positionId);
	
	EmployeeDTO getEmployeeByUserId(Long userId);

}
