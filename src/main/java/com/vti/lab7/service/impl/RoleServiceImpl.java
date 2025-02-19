package com.vti.lab7.service.impl;

import org.springframework.stereotype.Service;

import com.vti.lab7.model.Role;
import com.vti.lab7.repository.RoleRepository;
import com.vti.lab7.service.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;

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

}
