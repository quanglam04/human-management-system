package com.vti.lab7.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "permissions")
public class Permission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "permission_id")
	private Long permissionId;

	@Column(name = "permission_name", nullable = false, length = 255)
	private String permissionName;

	@Column(name = "description")
	private String description;

	@OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RolePermission> rolePermissions = new ArrayList<>();

	public Permission(String permissionName, String description) {
		this.permissionName = permissionName;
		this.description = description;
	}

}
