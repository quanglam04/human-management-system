package com.vti.lab7.dto.mapper;

import com.vti.lab7.dto.PositionDTO;
import com.vti.lab7.model.Position;

public class PositionMapperDTO {
	public static final PositionDTO convertPositionDTO(Position position) {
		return new PositionDTO(position.getPositionId(), position.getPositionName());
	}
}
