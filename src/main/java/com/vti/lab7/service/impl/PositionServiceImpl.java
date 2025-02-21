package com.vti.lab7.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vti.lab7.model.Position;
import com.vti.lab7.repository.PositionRepository;
import com.vti.lab7.service.PositionService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService{
	private final PositionRepository positionRepository;

 
	public Position findById(long id) {
		Optional<Position> positionOptional = positionRepository.findById(id);
		if(positionOptional.isPresent())
			return positionOptional.get();
		return null;
	}
	
	public List<Position> findAll(){
		return positionRepository.findAll();
	}

	public void deleteById(long id) {
		Optional<Position> positionOption = positionRepository.findById(id);
		if(positionOption.isPresent())
			positionRepository.delete(positionOption.get());
		return;
	}

	public Position createPosition(String name) {
		return positionRepository.save(new Position(name));
	}

	 
	public Position updatePosition(Position positionReq) {
		Optional<Position> positionOptional = positionRepository.findById(positionReq.getPositionId());
		if(positionOptional.isPresent()) {
			
			Position positionUpdate = positionOptional.get();  
	        positionUpdate.setPositionName(positionReq.getPositionName()); 
	        return positionRepository.save(positionUpdate);
		}
		return null;
	}
	
	
	
}
