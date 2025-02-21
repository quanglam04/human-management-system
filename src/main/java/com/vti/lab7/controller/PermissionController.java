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

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.vti.lab7.dto.PermissionDTO;
import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.service.PermissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

	private final PermissionService permissionService;

	@PreAuthorize("hasAuthority('read_permission')")
	@GetMapping
	public ResponseEntity<Object> getAllPermissions() {
		List<PermissionDTO> responseDto = permissionService.getAllPermissions();
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAuthority('read_permission')")
	@GetMapping("/{id}")
	public ResponseEntity<Object> getPermissionById(@PathVariable Long id) {
		PermissionDTO responseDto = permissionService.getPermissionById(id);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAuthority('create_permission')")
	@PostMapping
	public ResponseEntity<Object> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
		PermissionDTO responseDto = permissionService.createPermission(permissionDTO);
		RestData<?> restData = new RestData<>(201, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAuthority('update_permission')")
	@PutMapping("/{id}")
	public ResponseEntity<Object> updatePermission(@PathVariable Long id,
			@Valid @RequestBody PermissionDTO permissionDTO) {
		PermissionDTO responseDto = permissionService.updatePermission(id, permissionDTO);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@PreAuthorize("hasAuthority('delete_permission')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deletePermission(@PathVariable Long id) {
		permissionService.deletePermission(id);
		RestData<?> restData = new RestData<>(200, null,
				String.format("Permission with ID %d has been successfully deleted.", id), null);
		return ResponseEntity.ok().body(restData);
	}
}
