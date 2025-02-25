package com.vti.lab7.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.vti.lab7.model.Department;
import com.vti.lab7.repository.DepartmentRepository;
import com.vti.lab7.service.IDeparmentService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DepartmentService implements IDeparmentService {
	@Autowired
	private MessageSource messageSource;

	private String getMessage(String key) {
		return messageSource.getMessage(key, null, "Default message", LocaleContextHolder.getLocale());
	}
/*private String getMessage(String key) {
	    return messageSource.getMessage(key, null, Locale.JAPAN);
	}
*/
	@Autowired
	private DepartmentRepository departmentRepository;

	@Override
	public Optional<Department> findDepartment(Long id) {
		return Optional.ofNullable(departmentRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(getMessage("error.department.notfound"))));
	}

	@Override
	public List<Department> findAll() {
		List<Department> departments = departmentRepository.findAll();
		if (departments.isEmpty()) {
			throw new EntityNotFoundException(getMessage("error.department.notfound"));
		}
		return departments;
	}

	@Override
	public Department save(Department department) {
		if (departmentRepository.existsByName(department.getDepartmentName())) {
			throw new IllegalArgumentException(getMessage("error.department.name.exists"));
		}
		return departmentRepository.save(department);
	}

	@Override
	public Department update(Department department) {
		Department existingDepartment = departmentRepository.findById(department.getDepartmentId())
				.orElseThrow(() -> new EntityNotFoundException(getMessage("error.department.notfound")));
		if (!existingDepartment.getDepartmentName().equals(department.getDepartmentName())
				&& departmentRepository.existsByName(department.getDepartmentName())) {
			throw new IllegalArgumentException(getMessage("error.department.name.exists"));
		}
		existingDepartment.setDepartmentName(department.getDepartmentName());
		return departmentRepository.save(existingDepartment);
	}

	@Override
	public void deleteDepartment(Long id) {
		if (!departmentRepository.existsById(id)) {
			throw new EntityNotFoundException(getMessage("error.department.notfound"));
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
