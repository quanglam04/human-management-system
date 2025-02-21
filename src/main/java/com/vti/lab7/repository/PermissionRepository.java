package com.vti.lab7.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vti.lab7.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

	List<Permission> findByPermissionNameIn(List<String> names);

}
