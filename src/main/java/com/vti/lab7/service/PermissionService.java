package com.vti.lab7.service;

import java.util.List;

import com.vti.lab7.dto.PermissionDTO;
import com.vti.lab7.model.Role;

public interface PermissionService {
	public void init();

	public List<PermissionDTO> getAllPermissions();

	List<Role> findRolesByPermissionId(Long permissionId);

	public PermissionDTO getPermissionById(Long id);

	public void deletePermission(Long id);

	public PermissionDTO createPermission(PermissionDTO permissionDTO);

	public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO);

}
