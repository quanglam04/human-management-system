package com.vti.lab7.service;

import java.util.List;

import com.vti.lab7.model.Position;

public interface PositionService {
	Position findById(long id);

	List<Position> findAll();

	void deleteById(long id);

	Position createPosition(String positionName);

	Position updatePosition(Position positionReq);
}
