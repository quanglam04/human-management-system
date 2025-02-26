package com.vti.lab7.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vti.lab7.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

	List<Employee> findByDepartmentDepartmentId(Long departmentId);

	List<Employee> findByPositionPositionId(Long positionId);

	Optional<Employee> findByUserUserId(Long userId);

	Optional<Employee> findByUserUsername(String username);

	@Query("SELECT e.employeeId FROM Employee e JOIN e.user u WHERE u.username = :username")
	Long findEmployeeIdByUsername(@Param("username") String username);

}
