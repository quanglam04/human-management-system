package com.vti.lab7.service;

import java.util.List;

import com.vti.lab7.model.Role;

public interface RoleService {
	public void init();

	public List<Role> findAll();

	public Role findById(long id);

	public void deleteById(long id);
}
