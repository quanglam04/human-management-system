package com.vti.lab7.service;

import java.util.List;
import java.util.Optional;

import com.vti.lab7.model.RolePermission;
import com.vti.lab7.model.RolePermissionId;

public interface RolePermissionService {
	public void init();

	public List<RolePermission> findAll();

	public Optional<RolePermission> getPermissionById(RolePermissionId rolePermissionId);

	public RolePermission save(RolePermission rolePermission);

	public RolePermission update(RolePermission rolePermission);

	public int delete(RolePermissionId rolePermissionId);



}
