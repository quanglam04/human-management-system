package com.vti.lab7.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vti.lab7.dto.RolePermissionDTO;
import com.vti.lab7.model.RolePermission;
import com.vti.lab7.model.RolePermissionId;
import com.vti.lab7.service.impl.RolePermissionServiceImpl;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/v1/role-permissions")
@AllArgsConstructor
public class RolePermissionController {

	@Autowired
	private RolePermissionServiceImpl rolePermissionServiceImpl;

	@GetMapping()
	@PreAuthorize("hasAuthority('get_all_role_permissions')")
	public ResponseEntity<List<RolePermissionDTO>> findAll() {
		List<RolePermissionDTO> rolePermissionDTOs = rolePermissionServiceImpl.findAll().stream()
				.map(rp -> new RolePermissionDTO(rp.getRole().getRoleId(), rp.getRole().getRoleName(),
						rp.getPermission().getPermissionId(), rp.getPermission().getPermissionName()))
				.collect(Collectors.toList());

		if (rolePermissionDTOs.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(rolePermissionDTOs);
	}

	@GetMapping("/{roleId}/{permissionId}")
	@PreAuthorize("hasAuthority('get_role_permission_by_perrmission_id')")

	public ResponseEntity<RolePermission> findRolePermissions(@PathVariable Long roleId,
			@PathVariable Long permissionId) {

		RolePermissionId rolePermissionId = new RolePermissionId(roleId, permissionId);

		return Optional.ofNullable(rolePermissionServiceImpl.getPermissionById(rolePermissionId))
				.flatMap(rolePermission -> rolePermission).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PostMapping()
	@PreAuthorize("hasAuthority('create_new_role_permission')")
	public ResponseEntity<RolePermission> createDepartment(@RequestBody RolePermission rolePermission) {
		RolePermission savedRolePermission = rolePermissionServiceImpl.save(rolePermission);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedRolePermission);
	}

	@DeleteMapping("/{roleId}/{permissionId}")
	@PreAuthorize("hasAuthority('delete_role_permission')")
	public ResponseEntity<String> delete(@PathVariable Long roleId, @PathVariable Long permissionId) {
		RolePermissionId rolePermissionId = new RolePermissionId(roleId, permissionId);
		int numberOfRecordDelete = rolePermissionServiceImpl.delete(rolePermissionId);
		if (numberOfRecordDelete > 0) {
			return ResponseEntity.ok("Deleted successfully");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role-Permission not found");
		}
	}

}
