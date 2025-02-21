package com.vti.lab7.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vti.lab7.model.Permission;
import com.vti.lab7.model.Role;
import com.vti.lab7.model.RolePermission;
import com.vti.lab7.model.RolePermissionId;
import com.vti.lab7.repository.PermissionRepository;
import com.vti.lab7.repository.RolePermissionRepository;
import com.vti.lab7.repository.RoleRepository;
import com.vti.lab7.service.RolePermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
	private final RolePermissionRepository rolePermissionRepository;
	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;

	public void init() {
		if (rolePermissionRepository.count() == 0) {
			Role admin = roleRepository.findByRoleName("ADMIN");
			List<Permission> adminPermissions = permissionRepository.findAll();

			for (Permission permission : adminPermissions) {
				rolePermissionRepository.save(new RolePermission(
						new RolePermissionId(admin.getRoleId(), permission.getPermissionId()), admin, permission));
			}

			Role manager = roleRepository.findByRoleName("ADMIN");
			List<Permission> managerPermissions = permissionRepository
					.findByPermissionNameIn(List.of("employee.read", "employee.create", "employee.update",
							"employee.delete", "employee.department.read", "employee.position.read"));

			for (Permission permission : managerPermissions) {
				rolePermissionRepository.save(new RolePermission(
						new RolePermissionId(manager.getRoleId(), permission.getPermissionId()), manager, permission));
			}
		}
	}
}
