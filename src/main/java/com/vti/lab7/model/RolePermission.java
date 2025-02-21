package com.vti.lab7.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role_permission")
public class RolePermission {

	@EmbeddedId
	private RolePermissionId id;

	@ManyToOne
	@MapsId("roleId")
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@ManyToOne
	 @MapsId("permissionId") 
	@JoinColumn(name = "permission_id", nullable = false)
	private Permission permission;

}
