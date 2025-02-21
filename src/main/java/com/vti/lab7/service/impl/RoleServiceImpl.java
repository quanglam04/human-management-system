package com.vti.lab7.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

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

			Role role3 = new Role("EMPLOYER", "");
			roleRepository.save(role3);
		}

	}

	@Override
	public List<Role> findRolesByPermissionId(Long permissionId) {
		List<Role> listRoles = roleRepository.findRolesByPermissionId(permissionId);
		if (listRoles.isEmpty()) {
			throw new EntityNotFoundException(getMessage("error.roles.notfound"));
		}
		return listRoles;
	}

}
