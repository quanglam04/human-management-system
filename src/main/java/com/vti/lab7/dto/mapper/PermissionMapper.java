package com.vti.lab7.dto.mapper;

import com.vti.lab7.dto.PermissionDTO;
import com.vti.lab7.model.*;

public class PermissionMapper {

	public static PermissionDTO mapToDTO(Permission permission) {
		PermissionDTO dto = new PermissionDTO();
		dto.setPermissionId(permission.getPermissionId());
		dto.setPermissionName(permission.getPermissionName());
		dto.setDescription(permission.getDescription());
		return dto;
	}

	public static Permission mapToEntity(PermissionDTO dto) {
		Permission permission = new Permission();
		permission.setPermissionId(dto.getPermissionId());
		permission.setPermissionName(dto.getPermissionName());
		permission.setDescription(dto.getDescription());
		return permission;
	}

}
