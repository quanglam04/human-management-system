package com.vti.lab7.service;

import java.util.List;

import com.vti.lab7.model.Role;

import com.vti.lab7.dto.PermissionDTO;

public interface PermissionService {
	
	List<Role> findRolesByPermissionId(Long permissionId);

	public void init();

	public List<PermissionDTO> getAllPermissions();

	public PermissionDTO getPermissionById(Long id);

	public void deletePermission(Long id);

	public PermissionDTO createPermission(PermissionDTO permissionDTO);

	public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO);

}