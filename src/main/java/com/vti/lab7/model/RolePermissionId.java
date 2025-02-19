package com.vti.lab7.model;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionId implements Serializable {
	private Long roleId;
	private Long permissionId;

}
