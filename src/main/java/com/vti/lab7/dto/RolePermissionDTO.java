package com.vti.lab7.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RolePermissionDTO {
    private Long roleId;
    private String roleName;
    private Long permissionId;
    private String permissionName;


}
