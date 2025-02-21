package com.vti.lab7.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.service.annotation.DeleteExchange;

import com.vti.lab7.dto.RoleDTO;
import com.vti.lab7.dto.mapper.RoleMapperDTO;
import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.exception.custom.IdInvalidException;
import com.vti.lab7.model.Role;
import com.vti.lab7.service.impl.RoleServiceImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
	private final RoleServiceImpl roleServiceImpl;

	@GetMapping()
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
	public ResponseEntity<RestData<?>> getRoleById(@PathVariable long id)
			throws IdInvalidException, MethodArgumentTypeMismatchException {
		Role role = roleServiceImpl.findById(id);
		if (role == null)
			throw new IdInvalidException("ID invalid");
		RestData<RoleDTO> roleResponse = new RestData<>();
		roleResponse.setData(RoleMapperDTO.convertToRoleDTO(role));
		roleResponse.setError(null);
		roleResponse.setStatus(200);
		roleResponse.setMessage("Get role by id Success");
		return ResponseEntity.ok(roleResponse);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteRole(@PathVariable long id)
			throws MethodArgumentTypeMismatchException, IdInvalidException {
		Role role = roleServiceImpl.findById(id);
		if (role == null)
			throw new IdInvalidException("Id invalid");
		roleServiceImpl.deleteById(id);
		return ResponseEntity.ok("Delete role success");
	}
//	@PostMapping()
//	public ResponseEntity<T> createRole(@RequestBody Role role){
//		
//	}
//	
//	@PutMapping("/{id")
//	public ResponseEntity<T> updateRole(@PathVariable String id){
//		
//	}

}
