package com.vti.lab7.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.vti.lab7.constant.ErrorMessage;
import com.vti.lab7.dto.PermissionDTO;
import com.vti.lab7.dto.mapper.PermissionMapper;
import com.vti.lab7.exception.custom.ConflictException;
import com.vti.lab7.exception.custom.NotFoundException;
import com.vti.lab7.model.Permission;
import com.vti.lab7.model.Role;
import com.vti.lab7.repository.PermissionRepository;
import com.vti.lab7.service.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

	private Map<String, String> permissionMap = Map.ofEntries(
			// Department
			Map.entry("get_department_by_id", "Lấy văn phòng theo ID"),
			Map.entry("create_new_department", "Tạo văn phòng mới"),
			Map.entry("get_all_departments", "Lấy tất cả các văn phòng ra"),
			Map.entry("update_department", "Sửa các văn phòng cũ"),
			Map.entry("delete_department_by_id", "Xóa các văn phòng"),

			// Employee
			Map.entry("read_employee", "Xem danh sách tất cả nhân viên"),
			Map.entry("create_employee", "Tạo nhân viên mới với bất kỳ vai trò nào"),
			Map.entry("update_employee", "Cập nhật thông tin nhân viên"),
			Map.entry("delete_employee", "Xóa nhân viên khỏi hệ thống"),

			// Permission
			Map.entry("read_permission", "Xem danh sách và chi tiết quyền"),
			Map.entry("create_permission", "Tạo quyền mới"), Map.entry("update_permission", "Cập nhật thông tin quyền"),
			Map.entry("delete_permission", "Xóa quyền"),

			// Position
			Map.entry("position_delete_by_id", "Xóa một chức vụ theo ID"),
			Map.entry("position_create", "Tạo mới một chức vụ"),
			Map.entry("position_update_by_id", "Cập nhật thông tin của một chức vụ theo ID"),

			// Role
			Map.entry("role_read_all", "Lấy danh sách tất cả các vai trò"),
			Map.entry("role_read_role_by_id", "Lấy thông tin vai trò theo ID"),
			Map.entry("role_delete_role_by_id", "Xóa vai trò theo ID"), Map.entry("role_create", "Tạo mới vai trò"),
			Map.entry("role_update_role_by_id", "Cập nhật vai trò theo ID"),
			Map.entry("get_role_by_permissions_id", "Lấy danh sách role có quyền theo ID"),

			// Role-Permission
			Map.entry("get_all_role_permissions", "Lấy tất cả các quyền theo vai trò"),
			Map.entry("get_role_permission_by_perrmission_id", "Lấy quyền của vai trò theo ID quyền"),
			Map.entry("create_new_role_permission", "Tạo mới quyền cho vai trò"),
			Map.entry("delete_role_permission", "Xóa quyền của vai trò theo ID vai trò và ID quyền"),

			// User
			Map.entry("get_all_users", "Lấy danh sách tất cả người dùng"),
			Map.entry("get_department_users", "Lấy danh sách người dùng thuộc phòng ban của mình"),
			Map.entry("get_user_by_id", "Lấy thông tin của bất kỳ người dùng nào"),
			Map.entry("get_department_user_by_id", "Lấy thông tin của người dùng thuộc phòng ban của mình"),
			Map.entry("get_own_info", "Lấy thông tin của chính mình"),
			Map.entry("create_user_any_role", "Tạo người dùng mới với bất kỳ vai trò nào"),
			Map.entry("create_employee_in_department",
					"Tạo người dùng mới với vai trò Employee cho phòng ban của mình"),
			Map.entry("update_any_user", "Cập nhật thông tin của bất kỳ người dùng nào"),
			Map.entry("update_department_user", "Cập nhật thông tin của người dùng thuộc phòng ban của mình"),
			Map.entry("update_own_info", "Cập nhật thông tin của chính mình"),
			Map.entry("delete_any_user", "Xóa bất kỳ người dùng nào"));

	private final PermissionRepository permissionRepository;

	private Permission getEntity(Long id) {
		return permissionRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(ErrorMessage.Permission.ERR_NOT_FOUND_ID, id));
	}

	@Override
	public void init() {
		if (permissionRepository.count() == 0) {
			permissionMap.forEach((permissionName, description) -> {
				permissionRepository.save(new Permission(permissionName, description));
			});
		}
	}

	@Override
	public List<PermissionDTO> getAllPermissions() {
		return permissionRepository.findAll().stream().map(PermissionMapper::mapToDTO).toList();
	}

	@Override
	public PermissionDTO getPermissionById(Long id) {
		Permission permission = getEntity(id);
		return PermissionMapper.mapToDTO(permission);
	}

	@Override
	public void deletePermission(Long id) {
		Permission permission = getEntity(id);
		permissionRepository.delete(permission);
	}

	@Override
	public PermissionDTO createPermission(PermissionDTO permissionDTO) {
		Permission permission = PermissionMapper.mapToEntity(permissionDTO);
		if (permissionRepository.existsByPermissionName(permission.getPermissionName())) {
			throw new ConflictException(ErrorMessage.Permission.ERR_DUPLICATE_NAME, permission.getPermissionName());
		}

		permissionRepository.save(permission);
		return PermissionMapper.mapToDTO(permission);
	}

	@Override
	public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO) {
		Permission existingPermission = getEntity(id);

		if (!existingPermission.getPermissionName().equals(permissionDTO.getPermissionName())
				&& permissionRepository.existsByPermissionName(permissionDTO.getPermissionName())) {
			throw new ConflictException(ErrorMessage.Permission.ERR_DUPLICATE_NAME, permissionDTO.getPermissionName());
		}

		existingPermission.setPermissionName(permissionDTO.getPermissionName());
		existingPermission.setDescription(permissionDTO.getDescription());

		permissionRepository.save(existingPermission);

		return PermissionMapper.mapToDTO(existingPermission);
	}

	@Override
	public List<Role> findRolesByPermissionId(Long permissionId) {
		List<Role> roles = permissionRepository.findRolesByPermissionId(permissionId);
		if (roles.isEmpty()) {
			throw new NotFoundException(ErrorMessage.Permission.ERR_NOT_FOUND_ID, permissionId);
		}
		return roles;
	}
}