package com.vti.lab7.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vti.lab7.model.Permission;
import com.vti.lab7.model.RolePermission;
import com.vti.lab7.model.RolePermissionId;
import jakarta.transaction.Transactional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {

	@Query("SELECT rp FROM RolePermission rp WHERE rp.id.permissionId = ?1 AND rp.id.roleId = ?2")
	Optional<RolePermission> findPermissionById(Long permissionId, Long roleId);

	@Modifying
	@Transactional
	@Query("DELETE FROM RolePermission rp WHERE rp.id.permissionId = ?1 AND rp.id.roleId = ?2")
	int deleteRolePermission(Long permissionId, Long roleId);

	List<RolePermission> findById_RoleId(Long roleId);

	@Query("SELECT p FROM Permission p JOIN RolePermission rp ON p.permissionId = rp.id.permissionId WHERE rp.id.roleId = ?1")
	List<Permission> findPermissionsByRoleId(Long roleId);



}
