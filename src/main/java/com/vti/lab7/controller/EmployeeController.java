package com.vti.lab7.controller;

import com.vti.lab7.config.CustomUserDetails;
import com.vti.lab7.dto.EmployeeDTO;
import com.vti.lab7.dto.response.PaginationResponseDto;
import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Get all employees", description = "Retrieve all employees with optional filters")
    @PreAuthorize("hasAuthority('read_employee')")
    @GetMapping
    public ResponseEntity<Object> getAllEmployees(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String status,
            Pageable pageable,
            Authentication authentication
    ) {
        PaginationResponseDto<EmployeeDTO> responseDto = employeeService.getAllEmployees(firstName, lastName, phoneNumber, status, pageable, (CustomUserDetails) authentication.getPrincipal());

        RestData<?> restData = new RestData<>(200, null, null, responseDto);
        return ResponseEntity.ok().body(restData);
    }

    @Operation(summary = "Get employee by ID", description = "Retrieve a specific employee by their ID")
    @PreAuthorize("hasAuthority('read_employee')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getEmployeeById(@PathVariable Long id, Authentication authentication) {
        EmployeeDTO responseDto = employeeService.getEmployeeById(id, (CustomUserDetails) authentication.getPrincipal());

        RestData<?> restData = new RestData<>(200, null, null, responseDto);
        return ResponseEntity.ok().body(restData);
    }

    @Operation(summary = "Create new employee", description = "Add a new employee to the system")
    @PreAuthorize("hasAuthority('create_employee')")
    @PostMapping
    public ResponseEntity<Object> createEmployee(@Valid @RequestBody EmployeeDTO employee, Authentication authentication) {
        EmployeeDTO responseDto = employeeService.createEmployee(employee, (CustomUserDetails) authentication.getPrincipal());

        RestData<?> restData = new RestData<>(200, null, "Employee created successfully.", responseDto);
        return ResponseEntity.ok().body(restData);
    }

    @Operation(summary = "Update employee", description = "Update information of an existing employee")
    @PreAuthorize("hasAuthority('update_employee')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeDTO employee, Authentication authentication) {
        EmployeeDTO responseDto = employeeService.updateEmployee(id, employee, (CustomUserDetails) authentication.getPrincipal());

        RestData<?> restData = new RestData<>(200, null, "Employee updated successfully.", responseDto);
        return ResponseEntity.ok().body(restData);
    }

    @Operation(summary = "Delete employee", description = "Delete an employee by their ID")
    @PreAuthorize("hasAuthority('delete_employee')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteEmployee(@PathVariable Long id, Authentication authentication) {
        employeeService.deleteEmployee(id, (CustomUserDetails) authentication.getPrincipal());

        RestData<?> restData = new RestData<>(200, null, String.format("Employee with ID %d deleted successfully.", id), null);
        return ResponseEntity.ok().body(restData);
    }

    @Operation(summary = "Get employees by department", description = "Retrieve all employees of a specific department")
    @PreAuthorize("hasAuthority('read_employee')")
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<Object> getEmployeesByDepartment(@PathVariable Long departmentId, Authentication authentication) {
        List<EmployeeDTO> responseDto = employeeService.getEmployeesByDepartment(departmentId, (CustomUserDetails) authentication.getPrincipal());

        RestData<?> restData = new RestData<>(200, null, null, responseDto);
        return ResponseEntity.ok().body(restData);
    }

    @Operation(summary = "Get employees by position", description = "Retrieve all employees with a specific position")
    @PreAuthorize("hasAuthority('read_employee')")
    @GetMapping("/position/{positionId}")
    public ResponseEntity<Object> getEmployeesByPosition(@PathVariable Long positionId, Authentication authentication) {
        List<EmployeeDTO> responseDto = employeeService.getEmployeesByPosition(positionId, (CustomUserDetails) authentication.getPrincipal());

        RestData<?> restData = new RestData<>(200, null, null, responseDto);
        return ResponseEntity.ok().body(restData);
    }

}