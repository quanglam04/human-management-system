package com.vti.lab7.controller;

import com.vti.lab7.dto.PermissionDTO;
import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.model.Role;
import com.vti.lab7.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "APIs for managing permissions")
public class PermissionController {

	private final PermissionService permissionService;

	@Operation(summary = "Get all permissions", description = "Retrieve all available permissions")
	@PreAuthorize("hasAuthority('read_permission')")
	@GetMapping
	public ResponseEntity<Object> getAllPermissions() {
		List<PermissionDTO> responseDto = permissionService.getAllPermissions();
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@Operation(summary = "Get permission by ID", description = "Retrieve permission details by its ID")
	@PreAuthorize("hasAuthority('read_permission')")
	@GetMapping("/{id}")
	public ResponseEntity<Object> getPermissionById(@PathVariable Long id) {
		PermissionDTO responseDto = permissionService.getPermissionById(id);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@Operation(summary = "Create a new permission", description = "Add a new permission to the system")
	@PreAuthorize("hasAuthority('create_permission')")
	@PostMapping
	public ResponseEntity<Object> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
		PermissionDTO responseDto = permissionService.createPermission(permissionDTO);
		RestData<?> restData = new RestData<>(201, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@Operation(summary = "Update an existing permission", description = "Modify the details of an existing permission")
	@PreAuthorize("hasAuthority('update_permission')")
	@PutMapping("/{id}")
	public ResponseEntity<Object> updatePermission(@PathVariable Long id,
			@Valid @RequestBody PermissionDTO permissionDTO) {
		PermissionDTO responseDto = permissionService.updatePermission(id, permissionDTO);
		RestData<?> restData = new RestData<>(200, null, null, responseDto);
		return ResponseEntity.ok().body(restData);
	}

	@Operation(summary = "Delete a permission", description = "Remove a permission from the system by its ID")
	@PreAuthorize("hasAuthority('delete_permission')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deletePermission(@PathVariable Long id) {
		permissionService.deletePermission(id);
		RestData<?> restData = new RestData<>(200, null,
				String.format("Permission with ID %d has been successfully deleted.", id), null);
		return ResponseEntity.ok().body(restData);
	}

	@Operation(summary = "Get roles by permission ID", description = "Retrieve roles associated with a specific permission ID")
	@PostMapping("/{permissionId}/roles")
	@PreAuthorize("hasAuthority('get_role_by_permissions_id')")
	public ResponseEntity<List<Role>> getListRolesByPermissionId(@PathVariable Long permissionId) {
		List<Role> roles = permissionService.findRolesByPermissionId(permissionId);
		if (roles.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(roles);

	}
}
