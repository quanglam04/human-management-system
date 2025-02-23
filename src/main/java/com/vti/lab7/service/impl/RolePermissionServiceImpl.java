package com.vti.lab7.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vti.lab7.model.Permission;
import com.vti.lab7.model.Role;
import com.vti.lab7.model.RolePermission;
import com.vti.lab7.model.RolePermissionId;
import com.vti.lab7.repository.PermissionRepository;
import com.vti.lab7.repository.RolePermissionRepository;
import com.vti.lab7.repository.RoleRepository;
import com.vti.lab7.service.RolePermissionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
	private final RolePermissionRepository rolePermissionRepository;
	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;

	public void init() {
		if (rolePermissionRepository.count() == 0) {
			Role admin = roleRepository.findByRoleName("ADMIN").orElseThrow(() -> new EntityNotFoundException("khong tim thay role"));
			List<Permission> permissions = permissionRepository.findAll();

			for (Permission p : permissions) {
				rolePermissionRepository.save(
						new RolePermission(new RolePermissionId(admin.getRoleId(), p.getPermissionId()), admin, p));
			}
		}
	}

	@Override
	public List<RolePermission> findAll() {
		return rolePermissionRepository.findAll();
	}

	@Override
	public Optional<RolePermission> getPermissionById(RolePermissionId rolePermissionId) {
		return rolePermissionRepository.findPermissionById(rolePermissionId.getPermissionId(),
				rolePermissionId.getRoleId());
	}

	@Override
	public RolePermission update(RolePermission rolePermission) {
		return rolePermissionRepository.save(rolePermission);
	}

	public int delete(RolePermissionId rolePermissionId) {
		return rolePermissionRepository.deleteRolePermission(rolePermissionId.getPermissionId(),
				rolePermissionId.getRoleId());

	}

	@Override
	public RolePermission save(RolePermission rolePermission) {
		return rolePermissionRepository.save(rolePermission);
	}

	 public List<RolePermission> getPermissionsByRoleId(Long roleId) {
	        return rolePermissionRepository.findById_RoleId(roleId);
	    }

	@Override
	public List<Permission> findPermissionsByRoleId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Role> findRolesByPermissionId(Long permissionId) {
		// TODO Auto-generated method stub
		return null;
	}
}
