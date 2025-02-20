package com.vti.lab7.service;

import java.util.List;
import java.util.Optional;

import com.vti.lab7.model.Permission;
import com.vti.lab7.model.Role;
import com.vti.lab7.model.RolePermission;
import com.vti.lab7.model.RolePermissionId;

public interface RolePermissionService {
	public void init();

	public List<RolePermission> findAll();

	public Optional<RolePermission> getPermissionById(RolePermissionId rolePermissionId);

	public RolePermission save(RolePermission rolePermission);

	public RolePermission update(RolePermission rolePermission);

	public int delete(RolePermissionId rolePermissionId);

	public List<RolePermission> getPermissionsByRoleId(Long roleId);

	public List<Permission> findPermissionsByRoleId(Long id);

	public List<Role> findRolesByPermissionId(Long permissionId);

}
