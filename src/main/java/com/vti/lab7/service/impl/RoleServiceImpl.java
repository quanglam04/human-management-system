package com.vti.lab7.service.impl;

import java.util.List;
import java.util.Optional;

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
	
	public List<Role> findAll() {
		return roleRepository.findAll();
	}
	
	public Role findById(long id) {
		Optional<Role> roleOptional = roleRepository.findById(id);
		if(roleOptional.isPresent())
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

}
