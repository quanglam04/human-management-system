package com.vti.lab7.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vti.lab7.model.Role;
import com.vti.lab7.service.impl.PermissionServiceImpl;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
	private final PermissionServiceImpl permissionServiceImpl;

	@GetMapping
	public ResponseEntity<String> getPermissions() {
	    return ResponseEntity.ok("Danh sách quyền");
	}

	
	
	@PostMapping("/{permissionId}/roles")
	@PreAuthorize("hasAuthority('get_role_by_permissions_id')")
	public ResponseEntity<List<Role>> getListRolesByPermissionId(@PathVariable Long permissionId) {
		List<Role> roles = permissionServiceImpl.findRolesByPermissionId(permissionId);
		if (roles.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(roles);

	}
}
