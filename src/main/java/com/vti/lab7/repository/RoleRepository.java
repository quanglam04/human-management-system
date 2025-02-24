package com.vti.lab7.repository;


import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vti.lab7.model.Permission;
import com.vti.lab7.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByRoleName(String name);
	@Query("SELECT p FROM Permission p JOIN RolePermission rp ON p.permissionId = rp.id.permissionId WHERE rp.id.roleId = ?1")
	List<Permission> findPermissionsByRoleId(Long roleId);
}
