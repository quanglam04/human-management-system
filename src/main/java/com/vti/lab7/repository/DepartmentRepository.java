package com.vti.lab7.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vti.lab7.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
	@Query("SELECT COUNT(d) > 0 FROM Department d WHERE d.departmentName = ?1")
	boolean existsByName(String departmentName);

}
