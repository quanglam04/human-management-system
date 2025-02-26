package com.vti.lab7.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.vti.lab7.model.Employee;

public class EmployeeSpecification {

	public static Specification<Employee> hasFirstName(String firstName) {
		return (root, query, criteriaBuilder) -> {
			if (StringUtils.hasText(firstName)) {
				return criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
						"%" + firstName.toLowerCase() + "%");
			}
			return null;
		};
	}

	public static Specification<Employee> hasLastName(String lastName) {
		return (root, query, criteriaBuilder) -> {
			if (StringUtils.hasText(lastName)) {
				return criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
						"%" + lastName.toLowerCase() + "%");
			}
			return null;
		};
	}

	public static Specification<Employee> hasPhoneNumber(String phoneNumber) {
		return (root, query, criteriaBuilder) -> {
			if (StringUtils.hasText(phoneNumber)) {
				return criteriaBuilder.equal(root.get("phoneNumber"), phoneNumber);
			}
			return null;
		};
	}

	public static Specification<Employee> hasStatus(String status) {
		return (root, query, criteriaBuilder) -> {
			if (StringUtils.hasText(status)) {
				return criteriaBuilder.equal(root.get("status"), status);
			}
			return null;
		};
	}

	public static Specification<Employee> belongsToDepartment(Long departmentId) {
		return (root, query, criteriaBuilder) -> {
			if (departmentId != null) {
				return criteriaBuilder.equal(root.get("department").get("departmentId"), departmentId);
			}
			return null;
		};
	}

	public static Specification<Employee> belongsToPosition(Long positionId) {
		return (root, query, criteriaBuilder) -> {
			if (positionId != null) {
				return criteriaBuilder.equal(root.get("position").get("positionId"), positionId);
			}
			return null;
		};
	}
}
