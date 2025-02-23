package com.vti.lab7.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vti.lab7.model.Position;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
	Optional<Position> findByPositionName(String positionName);
}
