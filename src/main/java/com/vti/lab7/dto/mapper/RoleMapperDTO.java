package com.vti.lab7.dto.mapper;

import com.vti.lab7.dto.RoleDTO;
import com.vti.lab7.model.Role;

public class RoleMapperDTO {
	public static final RoleDTO convertToRoleDTO(Role role) {
		return new RoleDTO(role.getRoleId(), role.getDescription(), role.getRoleName());
	}

}
