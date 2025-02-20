package com.vti.lab7.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vti.lab7.model.Department;
import com.vti.lab7.repository.DepartmentRepository;
import com.vti.lab7.service.IDeparmentService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DepartmentService implements IDeparmentService {

	@Autowired
	private DepartmentRepository departmentRepository;

	@Override
	public Optional<Department> findDepartment(Long id) {
		return departmentRepository.findById(id);
	}

	@Override
	public List<Department> findAll() {
		return departmentRepository.findAll();
	}

	@Override
	public Department save(Department department) {
		return departmentRepository.save(department);
	}

	@Override
	public Department update(Department department) {
		return departmentRepository.save(department);
	}

	@Override
	public void deleteDepartment(Long id) {
		if (!departmentRepository.existsById(id)) {
			throw new EntityNotFoundException("Department not found with ID: " + id);
		}
		departmentRepository.deleteById(id);
	}

}
