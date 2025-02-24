package com.vti.lab7.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.vti.lab7.config.CustomUserDetails;
import com.vti.lab7.dto.EmployeeDTO;
import com.vti.lab7.dto.response.PaginationResponseDto;
import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;

	@PreAuthorize("hasAnyAuthority('employee_read_all', 'employee_read_department')")
	@GetMapping
	public ResponseEntity<Object> getAllEmployees(@RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName, @RequestParam(required = false) String phoneNumber,
			@RequestParam(required = false) String status, Pageable pageable, Authentication authentication) {
		CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

		PaginationResponseDto<EmployeeDTO> responseDto = employeeService.getAllEmployees(firstName, lastName,
				phoneNumber, status, pageable, currentUser);

		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAnyAuthority('employee_read_all', 'employee_read_department', 'employee_read_self')")
	@GetMapping("/{id}")
	public ResponseEntity<Object> getEmployeeById(@PathVariable Long id, Authentication authentication) {
		CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

		EmployeeDTO responseDto = employeeService.getEmployeeById(id, currentUser);

		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAnyAuthority('employee_create_all', 'employee_create_department')")
	@PostMapping
	public ResponseEntity<Object> createEmployee(@Valid @RequestBody EmployeeDTO employee,
			Authentication authentication) {

		CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

		EmployeeDTO responseDto = employeeService.createEmployee(employee, currentUser);

		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAnyAuthority('employee_update_all', 'employee_update_department', 'employee_update_self')")
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeDTO employee,
			Authentication authentication) {
		CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

		EmployeeDTO responseDto = employeeService.updateEmployee(id, employee, currentUser);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);

		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAuthority('employee_delete_all')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteEmployee(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
		RestData<?> restData = new RestData<>(200, null, String.format("Employee with ID %d deleted successfully.", id),
				null);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAuthority('read_employee_by_department')")
	@GetMapping("/department/{departmentId}")
	public ResponseEntity<Object> getEmployeesByDepartment(@PathVariable Long departmentId) {
		List<EmployeeDTO> responseDto = employeeService.getEmployeesByDepartment(departmentId);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAuthority('read_employee_by_position')")
	@GetMapping("/position/{positionId}")
	public ResponseEntity<Object> getEmployeesByPosition(@PathVariable Long positionId) {
		List<EmployeeDTO> responseDto = employeeService.getEmployeesByPosition(positionId);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

}