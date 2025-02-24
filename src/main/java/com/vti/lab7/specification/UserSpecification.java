package com.vti.lab7.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.vti.lab7.model.Employee;
import com.vti.lab7.model.User;

import jakarta.persistence.criteria.Join;

public class UserSpecification {
	public static Specification<User> hasUsername(String username) {
		return (root, query, criteriaBuilder) -> {
			if (StringUtils.hasText(username)) {
				return criteriaBuilder.like(criteriaBuilder.lower(root.get("username")),
						"%" + username.toLowerCase() + "%");
			}
			return null;
		};
	}
	
	public static Specification<User> hasEmail(String email) {
		return (root, query, criteriaBuilder) -> {
			if (StringUtils.hasText(email)) {
				return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
						"%" + email.toLowerCase() + "%");
			}
			return null;
		};
	}
	
	public static Specification<User> belongsToDepartment(Long departmentId) {
	    return (root, query, criteriaBuilder) -> {
	        if (departmentId != null) {
	            Join<User, Employee> employeeJoin = root.join("employee");
	            return criteriaBuilder.equal(employeeJoin.get("department").get("departmentId"), departmentId);
	        }
	        return null;
	    };
	}

}
