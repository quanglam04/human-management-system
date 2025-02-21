package com.vti.lab7.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vti.lab7.model.Permission;
import com.vti.lab7.model.Role;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

	List<Permission> findByPermissionNameIn(List<String> names);


	@Query("SELECT r FROM Role r JOIN RolePermission rp ON r.roleId = rp.id.roleId WHERE rp.id.permissionId = ?1")
	List<Role> findRolesByPermissionId(Long permissionId);

	boolean existsByPermissionName(String name);
}
