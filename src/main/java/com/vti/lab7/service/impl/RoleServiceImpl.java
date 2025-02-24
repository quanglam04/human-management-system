package com.vti.lab7.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.vti.lab7.model.Permission;
import com.vti.lab7.model.Role;
import com.vti.lab7.repository.RoleRepository;
import com.vti.lab7.service.RoleService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;
	@Autowired
	private MessageSource messageSource;

	private String getMessage(String key) {
		return messageSource.getMessage(key, null, "Default message", LocaleContextHolder.getLocale());
	}

	public void init() {
		if (roleRepository.count() == 0) {
			Role role1 = new Role("ADMIN", "");
			roleRepository.save(role1);

			Role role2 = new Role("MANAGER", "");
			roleRepository.save(role2);

			Role role3 = new Role("EMPLOYEE", "");
			roleRepository.save(role3);
		}

	}

	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	public Role findById(long id) {
		Optional<Role> roleOptional = roleRepository.findById(id);
		if (roleOptional.isPresent())
			return roleOptional.get();
		return null;
	}

	public void deleteById(long id) {
		roleRepository.deleteById(id);
	}
	
	public Role createRole(Role role) {
		return roleRepository.save(role);
	}
	
	public Role updateRole(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public List<Permission> findPermissionsByRoleId(Long roleId) {
		List<Permission> permissions = roleRepository.findPermissionsByRoleId(roleId);
		if (permissions.isEmpty()) {
			throw new EntityNotFoundException(getMessage("error.permissions.notfound"));
		}
		return permissions;
	}

}
