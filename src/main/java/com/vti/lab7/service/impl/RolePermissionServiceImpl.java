package com.vti.lab7.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.vti.lab7.constant.ErrorMessage;
import com.vti.lab7.exception.custom.ConflictException;
import com.vti.lab7.exception.custom.NotFoundException;
import com.vti.lab7.model.Permission;
import com.vti.lab7.model.Role;
import com.vti.lab7.model.RolePermission;
import com.vti.lab7.model.RolePermissionId;
import com.vti.lab7.repository.PermissionRepository;
import com.vti.lab7.repository.RolePermissionRepository;
import com.vti.lab7.repository.RoleRepository;
import com.vti.lab7.service.RolePermissionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
	private final RolePermissionRepository rolePermissionRepository;
	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;

	public void init() {
		if (rolePermissionRepository.count() == 0) {

			Role admin = roleRepository.findByRoleName("ADMIN")
					.orElseThrow(() -> new RuntimeException("User not found"));
			List<Permission> adminPermissions = permissionRepository.findAll();

			for (Permission permission : adminPermissions) {
				rolePermissionRepository.save(new RolePermission(
						new RolePermissionId(admin.getRoleId(), permission.getPermissionId()), admin, permission));
			}

			Role manager = roleRepository.findByRoleName("ADMIN")
					.orElseThrow(() -> new RuntimeException("User not found"));
			List<Permission> managerPermissions = permissionRepository
					.findByPermissionNameIn(List.of("employee.read", "employee.create", "employee.update",
							"employee.delete", "employee.department.read", "employee.position.read"));

			for (Permission permission : managerPermissions) {
				rolePermissionRepository.save(new RolePermission(
						new RolePermissionId(manager.getRoleId(), permission.getPermissionId()), manager, permission));
			}
		}
	}

	@Override
	public List<RolePermission> findAll() {
		List<RolePermission> rolePermissions = rolePermissionRepository.findAll();
		if (rolePermissions.isEmpty()) {
			throw new NotFoundException(ErrorMessage.RolePermissions.ERR_NOT_FOUND_ID);
		}
		return rolePermissions;
	}

	@Override
	public Optional<RolePermission> getPermissionById(RolePermissionId rolePermissionId) {
		return Optional.ofNullable(rolePermissionRepository
				.findPermissionById(rolePermissionId.getPermissionId(), rolePermissionId.getRoleId())
				.orElseThrow(() -> new NotFoundException(ErrorMessage.RolePermissions.ERR_NOT_FOUND_ID)));
	}

	@Override
	public RolePermission save(RolePermission rolePermission) {
		Optional<RolePermission> existRolePermission = rolePermissionRepository
				.findPermissionById(rolePermission.getId().getPermissionId(), rolePermission.getId().getRoleId());
		if (!existRolePermission.isEmpty()) {
			throw new ConflictException(ErrorMessage.RolePermissions.ERR_DUPLICATE_ROLE_PERMISSION);
		}
		return rolePermissionRepository.save(rolePermission);
	}

	@Override
	public RolePermission update(RolePermission rolePermission) {
		Optional<RolePermission> existRolePermission = rolePermissionRepository
				.findPermissionById(rolePermission.getId().getPermissionId(), rolePermission.getId().getRoleId());
		if (existRolePermission.isEmpty()) {
			throw new NotFoundException(ErrorMessage.RolePermissions.ERR_NOT_FOUND_ID);
		}
		return rolePermissionRepository.save(rolePermission);
	}

	public int delete(RolePermissionId rolePermissionId) {
		Optional<RolePermission> existRolePermission = rolePermissionRepository
				.findPermissionById(rolePermissionId.getPermissionId(), rolePermissionId.getRoleId());
		if (existRolePermission.isEmpty()) {
			throw new NotFoundException(ErrorMessage.RolePermissions.ERR_NOT_FOUND_ID);
		}
		return rolePermissionRepository.deleteRolePermission(rolePermissionId.getPermissionId(),
				rolePermissionId.getRoleId());
	}

	public List<RolePermission> getPermissionsByRoleId(Long roleId) {
		return rolePermissionRepository.findById_RoleId(roleId);
	}
}
