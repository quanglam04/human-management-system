package com.vti.lab7.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vti.lab7.constant.ErrorMessage;
import com.vti.lab7.exception.custom.ConflictException;
import com.vti.lab7.exception.custom.NotFoundException;
import com.vti.lab7.model.Department;
import com.vti.lab7.repository.DepartmentRepository;
import com.vti.lab7.service.IDeparmentService;

@Service
public class DepartmentService implements IDeparmentService {
	
	@Autowired
	private DepartmentRepository departmentRepository;

	@Override
	public Optional<Department> findDepartment(Long id) {
		return Optional.ofNullable(departmentRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND_ID, id)));
	}

	@Override
	public List<Department> findAll() {
		List<Department> departments = departmentRepository.findAll();
		if (departments.isEmpty()) {
			throw new NotFoundException("error.department.notfound");
		}
		return departments;
	}

	@Override
	public Department save(Department department) {
		if (departmentRepository.existsByName(department.getDepartmentName())) {
			throw new ConflictException("error.department.name.exists");
		}
		return departmentRepository.save(department);
	}

	@Override
	public Department update(Department department) {
		Department existingDepartment = departmentRepository.findById(department.getDepartmentId())
				.orElseThrow(() -> new NotFoundException("error.department.notfound"));
		if (!existingDepartment.getDepartmentName().equals(department.getDepartmentName())
				&& departmentRepository.existsByName(department.getDepartmentName())) {
			throw new ConflictException("error.department.name.exists");
		}
		existingDepartment.setDepartmentName(department.getDepartmentName());
		return departmentRepository.save(existingDepartment);
	}

	@Override
	public void deleteDepartment(Long id) {
		if (!departmentRepository.existsById(id)) {
			throw new NotFoundException("error.department.notfound");
		}
		
		if(departmentRepository.countEmployeesByDepartmentId(id) != 0) {
			throw new ConflictException(ErrorMessage.Department.ERR_HAS_EMPLOYEE);
		}
		departmentRepository.deleteById(id);
	}

	@Override
	public void init() {
	  if (departmentRepository.count() == 0) {
            // Tạo danh sách 20 phòng ban
            List<Department> departments = IntStream.rangeClosed(1, 20).mapToObj(i -> {
                Department department = new Department();
                department.setDepartmentName("Department " + i);
                department.setDescription("Description for Department " + i);
                return department;
            }).toList();

            departmentRepository.saveAll(departments);
        }
	}
}
