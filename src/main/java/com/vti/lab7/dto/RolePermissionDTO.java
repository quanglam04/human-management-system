package com.vti.lab7.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionDTO {
    private Long roleId;
    private Long permissionId;

}
