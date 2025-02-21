package com.vti.lab7.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

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
	@PreAuthorize("hasAuthority('employee.read')")
	@GetMapping
	public ResponseEntity<Object> getAllEmployees(@RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName, @RequestParam(required = false) String phoneNumber,
			@RequestParam(required = false) String status, Pageable pageable) {
		PaginationResponseDto<EmployeeDTO> responseDto = employeeService.getAllEmployees(firstName, lastName,
				phoneNumber, status, pageable);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}


	@PreAuthorize("hasAuthority('employee.read')")

	@GetMapping("/{id}")
	public ResponseEntity<Object> getEmployeeById(@PathVariable Long id) {
		EmployeeDTO responseDto = employeeService.getEmployeeById(id);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}


	@PreAuthorize("hasAuthority('employee.create')")

	@PostMapping
	public ResponseEntity<Object> createEmployee(@Valid @RequestBody EmployeeDTO employee) {
		EmployeeDTO responseDto = employeeService.createEmployee(employee);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAuthority('employee.update')")
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeDTO employee) {
		EmployeeDTO responseDto = employeeService.updateEmployee(id, employee);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}


	@PreAuthorize("hasAuthority('employee.delete')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteEmployee(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
		RestData<?> restData = new RestData<>(200, null, String.format("Employee with ID %d deleted successfully.", id),
				null);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAuthority('employee.department.read')")

	@GetMapping("/department/{departmentId}")
	public ResponseEntity<Object> getEmployeesByDepartment(@PathVariable Long departmentId) {
		List<EmployeeDTO> responseDto = employeeService.getEmployeesByDepartment(departmentId);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAuthority('employee.position.read')")

	@GetMapping("/position/{positionId}")
	public ResponseEntity<Object> getEmployeesByPosition(@PathVariable Long positionId) {
		List<EmployeeDTO> responseDto = employeeService.getEmployeesByPosition(positionId);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

}
