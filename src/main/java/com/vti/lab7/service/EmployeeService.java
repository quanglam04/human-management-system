package com.vti.lab7.service;

import com.vti.lab7.config.CustomUserDetails;
import com.vti.lab7.dto.EmployeeDTO;
import com.vti.lab7.dto.response.PaginationResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    void init();

    PaginationResponseDto<EmployeeDTO> getAllEmployees(String firstName, String lastName, String phoneNumber, String status, Pageable pageable, CustomUserDetails userDetails);

    EmployeeDTO getEmployeeById(Long id, CustomUserDetails userDetails);

    EmployeeDTO createEmployee(EmployeeDTO employee, CustomUserDetails userDetails);

    EmployeeDTO updateEmployee(Long id, EmployeeDTO employee, CustomUserDetails userDetails);

    void deleteEmployee(Long id, CustomUserDetails userDetails);

    List<EmployeeDTO> getEmployeesByDepartment(Long departmentId, CustomUserDetails userDetails);

    List<EmployeeDTO> getEmployeesByPosition(Long positionId, CustomUserDetails userDetails);

}
