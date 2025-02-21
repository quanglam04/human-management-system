package com.vti.lab7.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vti.lab7.model.Permission;
import com.vti.lab7.repository.PermissionRepository;
import com.vti.lab7.service.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

	private List<String> permission_names = List.of(
			"get_all_users", 
			"get_department_users", 
			"get_own_info",
			"get_user_by_id", 
			"get_department_user_by_id", 
			"create_user_any_role", 
			"create_employee_in_department",
			"update_any_user", 
			"update_department_user", 
			"update_own_info", 
			"delete_any_user", 
			"get_all_departments",
			"create_new_departments", 
			"update_departments", 
			"delete_departments", 
			"get_all_role_permissions",
			"delete_any_user",
			"employee.read", 
			"employee.create", 
			"employee.update", 
			"employee.delete", 
			"employee.department.read",
			"employee.position.read",
			"role.readAll",
			"role.readRoleByID",
			"role.create",
			"role.updateRoleByID",
			"role.deleteRoleByID"
			);
			
	private List<String> descriptions = List.of(
			"Lấy danh sách tất cả người dùng",
			"Lấy danh sách người dùng thuộc phòng ban của mình", 
			"Lấy thông tin của chính mình",
			"Lấy thông tin của bất kỳ người dùng nào", 
			"Lấy thông tin của người dùng thuộc phòng ban của mình",
			"Tạo người dùng mới với bất kỳ vai trò nào",
			"Tạo người dùng mới với vai trò Employee cho phòng ban của mình",
			"Cập nhật thông tin của bất kỳ người dùng nào",
			"Cập nhật thông tin của người dùng thuộc phòng ban của mình", 
			"Cập nhật thông tin của chính mình",
			"Xóa bất kỳ người dùng nào", 
			"Lấy tất cả các văn phòng ra", 
			"Tạo văn phòng mới", 
			"Sửa các văn phòng cũ",
			"Xóa các văn phòng", 
			"Lấy danh sách tất cả các mối quan hệ giữa vai trò và quyền",
			"Xóa bất kỳ người dùng nào",
			"Xem danh sách nhân viên", 
			"Tạo mới nhân viên", 
			"Cập nhật thông tin nhân viên", 
			"Xóa nhân viên",
			"Xem danh sách nhân viên trong phòng ban", 
			"Xem danh sách nhân viên theo vị trí",
			"Lấy danh sách tất cả các vai trò",
			"Lấy thông tin chi tiết của 1 vai trò theo ID",
			"Tạo một vai trò mới",
			"Cập nhật thông tin vai trò theo ID",
			"Xóa một vai trò theo ID"
			);
	private final PermissionRepository permissionRepository;

	public void init() {
		if (permission_names.size() != descriptions.size()) {
			throw new IllegalStateException(
					"Số lượng permission_names và descriptions không khớp! " + "permission_names.size() = "
							+ permission_names.size() + ", descriptions.size() = " + descriptions.size());
		}

		if (permissionRepository.count() == 0) {
			for (int i = 0; i < permission_names.size(); i++) {
				permissionRepository.save(new Permission(permission_names.get(i), descriptions.get(i)));
			}
		}
	}
}
