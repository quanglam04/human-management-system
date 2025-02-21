package com.vti.lab7.service;

import java.util.List;

import com.vti.lab7.model.Role;

public interface PermissionService  {
	public void init();
	List<Role> findRolesByPermissionId(Long permissionId);
}
