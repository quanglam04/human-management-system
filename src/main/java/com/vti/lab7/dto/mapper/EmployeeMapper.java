package com.vti.lab7.dto.mapper;

import com.vti.lab7.dto.EmployeeDTO;
import com.vti.lab7.model.Employee;

public class EmployeeMapper {

	public static EmployeeDTO convertToDTO(Employee employee) {
		return new EmployeeDTO(employee.getEmployeeId(), employee.getFirstName(), employee.getLastName(),
				employee.getDateOfBirth(), employee.getPhoneNumber(), employee.getAddress(), employee.getHireDate(),
				employee.getSalary(), employee.getStatus(), employee.getUser().getUserId(),
				employee.getPosition().getPositionId(), employee.getDepartment().getDepartmentId());
	}

	public static Employee convertToEntity(EmployeeDTO dto) {
		return new Employee(dto.getEmployeeId(), dto.getFirstName(), dto.getLastName(), dto.getDateOfBirth(),
				dto.getPhoneNumber(), dto.getAddress(), dto.getHireDate(), dto.getSalary(), dto.getStatus(), null, null,
				null);
	}
}
