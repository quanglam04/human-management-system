package com.vti.lab7.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.vti.lab7.dto.RoleDTO;
import com.vti.lab7.dto.mapper.RoleMapperDTO;
import com.vti.lab7.dto.request.RoleRequestDTO;
import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.exception.custom.ConflictException;
import com.vti.lab7.exception.custom.NotFoundException;
import com.vti.lab7.model.Role;
import com.vti.lab7.model.Permission;
import com.vti.lab7.service.impl.RoleServiceImpl;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
	private final RoleServiceImpl roleServiceImpl;
	@GetMapping()
	@PreAuthorize("hasAuthority('role.readAll')")
	public ResponseEntity<RestData<List<RoleDTO>>> getAllRole() {
		List<Role> roles = roleServiceImpl.findAll();
		List<RoleDTO> rolesDTO = roles.stream().map(RoleMapperDTO::convertToRoleDTO).collect(Collectors.toList());
		RestData<List<RoleDTO>> restData = new RestData<>();
		restData.setError(null);
		restData.setData(rolesDTO);
		restData.setMessage("get all role success");
		restData.setStatus(200);
		return ResponseEntity.ok(restData);

	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('role.readRoleByID')")
	public ResponseEntity<RestData<?>> getRoleById(@PathVariable long id) throws MethodArgumentTypeMismatchException{
		Role role = roleServiceImpl.findById(id);
		if (role == null)
			throw new NotFoundException("ID invalid");
		RestData<RoleDTO> roleResponse = new RestData<>();
		roleResponse.setData(RoleMapperDTO.convertToRoleDTO(role));
		roleResponse.setError(null);
		roleResponse.setStatus(200);
		roleResponse.setMessage("Get role by id Success");
		return ResponseEntity.ok(roleResponse);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('role.deleteRoleByID')")
	public ResponseEntity<String> deleteRole(@PathVariable long id) throws MethodArgumentTypeMismatchException{

		Role role = roleServiceImpl.findById(id);
		if (role == null)
			throw new NotFoundException("Id invalid");
		roleServiceImpl.deleteById(id);
		return ResponseEntity.ok("Delete role success");
	}

	@PostMapping()
	@PreAuthorize("hasAuthority('role.create')")
	public ResponseEntity<RestData<RoleDTO>> createRole(@RequestBody RoleRequestDTO roleRequestDTO) throws ConflictException{
		RestData<RoleDTO> restData = new RestData<>();
		Role role = new Role();
		String roleNameRequest = roleRequestDTO.getRoleName();
		List<String> roleNames = roleServiceImpl.findAll().stream().map(x -> x.getRoleName()).toList();
		for(String roleName : roleNames )
			if(roleName.toLowerCase().trim().equals(roleNameRequest.toLowerCase().trim()))
				throw new ConflictException("error.role.name.exist",roleName );
		role.setDescription(roleRequestDTO.getDescription());
		role.setRoleName(roleRequestDTO.getRoleName());
		
		RoleDTO roleDTO = RoleMapperDTO.convertToRoleDTO(roleServiceImpl.createRole(role))  ;
		restData.setData(roleDTO);
		restData.setError(null);
		restData.setMessage("create role success");
		restData.setStatus(HttpStatus.CREATED.value());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(restData);
		
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('role.updateRoleByID')")
	public ResponseEntity<RestData<RoleDTO>> updateRole(@PathVariable long id, @RequestBody RoleRequestDTO roleRequestDTO) throws ConflictException {
		Role role = roleServiceImpl.findById(id);
		RestData<RoleDTO> restData = new RestData<>();
		if(role == null)
			throw new NotFoundException("ID invalid");
		
		List<String> roleNames = roleServiceImpl.findAll().stream().map(x -> x.getRoleName()).toList();
		for(String roleName : roleNames )
			if(roleName.toLowerCase().equals(roleRequestDTO.getRoleName().toLowerCase()))
				throw new ConflictException("error.role.name.exist",roleName );
		role.setDescription(roleRequestDTO.getDescription().trim());
		role.setRoleName(roleRequestDTO.getRoleName().trim());
		roleServiceImpl.updateRole(role);
		restData.setData(RoleMapperDTO.convertToRoleDTO(role));
		restData.setStatus(HttpStatus.OK.value());
		restData.setError(null);
		restData.setMessage("update role success");
		
		return ResponseEntity.ok(restData);
			
	}
	
	@GetMapping("/{roleId}/permissions")
	@PreAuthorize("hasAuthority('get_permissions_by_role_id')")
	public ResponseEntity<List<Permission>> getListRolesByPermissionId(@PathVariable Long roleId) {
		List<Permission> permissions = roleServiceImpl.findPermissionsByRoleId(roleId);
		if (permissions.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(permissions);

	}

}
