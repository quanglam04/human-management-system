package com.vti.lab7.service;

import java.util.List;
import java.util.Optional;

import com.vti.lab7.model.Department;

public interface IDeparmentService {

	Optional<Department> findDepartment(Long id);

	List<Department> findAll();

	Department save(Department department);

	Department update(Department department);

	void deleteDepartment(Long id);
}
