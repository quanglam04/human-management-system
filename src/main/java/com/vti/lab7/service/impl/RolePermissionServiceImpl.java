package com.vti.lab7.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.vti.lab7.constant.RoleConstants;
import com.vti.lab7.dto.RolePermissionDTO;
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

	private void assignPermissionsToRole(String roleName, List<Permission> permissions) {
		Role role = roleRepository.findByRoleName(roleName)
				.orElseThrow(() -> new RuntimeException("Role " + roleName + " not found!"));

		List<RolePermission> rolePermissions = permissions.stream()
				.map(permission -> new RolePermission(
						new RolePermissionId(role.getRoleId(), permission.getPermissionId()), role, permission))
				.toList();

		rolePermissionRepository.saveAll(rolePermissions);
	}

	public void init() {
		if (rolePermissionRepository.count() == 0) {
			assignPermissionsToRole(RoleConstants.ADMIN, permissionRepository.findAll());

			assignPermissionsToRole(RoleConstants.MANAGER, permissionRepository.findByPermissionNameIn(List.of(
					// Department
					"get_all_departments", "get_department_by_id",

					// Employee
					"read_employee", "create_employee", "update_employee",

					// Role
					"role_read_all", "role_read_role_by_id", "get_role_by_permissions_id",

					// Role-Permission
					"get_all_role_permissions", "get_role_permission_by_perrmission_id",

					// User
					"get_department_users", "get_department_user_by_id", "get_own_info",
					"create_employee_in_department", "update_department_user", "update_own_info")));

			assignPermissionsToRole(RoleConstants.EMPLOYEE, permissionRepository.findByPermissionNameIn(List.of(
					// Employee
					"read_employee", "update_employee",

					// User
					"get_own_info", "update_own_info")));
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

	public RolePermission saveRolePermission(RolePermissionDTO rolePermissionDTO) {
		Optional<RolePermission> existRolePermission = rolePermissionRepository
				.findPermissionById(rolePermissionDTO.getPermissionId(), rolePermissionDTO.getRoleId());

		if (existRolePermission.isPresent()) {
			throw new ConflictException(ErrorMessage.RolePermissions.ERR_DUPLICATE_ROLE_PERMISSION);
		}

		Role role = roleRepository.findById(rolePermissionDTO.getRoleId())
				.orElseThrow(() -> new NotFoundException(ErrorMessage.Role.ERR_NOT_FOUND_ID));

		Permission permission = permissionRepository.findById(rolePermissionDTO.getPermissionId())
				.orElseThrow(() -> new NotFoundException(ErrorMessage.Permission.ERR_NOT_FOUND_ID));

		RolePermission rolePermission = new RolePermission(
				new RolePermissionId(role.getRoleId(), permission.getPermissionId()), role, permission);

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

	@Override
	public RolePermission save(RolePermission rolePermission) {
		// TODO Auto-generated method stub
		return null;
	}
}
