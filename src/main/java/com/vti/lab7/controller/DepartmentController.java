package com.vti.lab7.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.model.Department;
import com.vti.lab7.service.impl.DepartmentService;

@RestController
@RequestMapping("api/v1/departments")
public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('get_department_by_id')")
	public ResponseEntity<Department> findDepartment(@PathVariable Long id) {
		Optional<Department> departmentOptional = departmentService.findDepartment(id);
		return departmentOptional.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping()
	@PreAuthorize("hasAuthority('create_new_department')")
	public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
		Department savedDepartment = departmentService.save(department);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedDepartment);
	}

	@GetMapping()
	@PreAuthorize("hasAuthority('get_all_departments')")
	public ResponseEntity<List<Department>> findAll() {
		List<Department> departments = departmentService.findAll();
		if (departments.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(departments);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('update_department')")
	public ResponseEntity<Department> updateDepartment(@PathVariable long id, @RequestBody Department department) {
		Optional<Department> optionalDepartment = departmentService.findDepartment(id);

		if (!optionalDepartment.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		Department existingDepartment = optionalDepartment.get();
		existingDepartment.setDepartmentName(department.getDepartmentName());
		existingDepartment.setDescription(department.getDescription());

		departmentService.save(existingDepartment);

		return ResponseEntity.ok(existingDepartment);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('delete_department_by_id')")
	public ResponseEntity<Object> deleteDepartment(@PathVariable Long id) {
		departmentService.deleteDepartment(id);
		RestData<?> restData = new RestData<>(200, null, null, true);
		return ResponseEntity.ok().body(restData);

	}

}
