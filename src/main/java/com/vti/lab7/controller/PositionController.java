package com.vti.lab7.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.vti.lab7.dto.PositionDTO;
import com.vti.lab7.dto.mapper.PositionMapperDTO;
import com.vti.lab7.dto.request.PositionRequestDTO;
import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.exception.custom.NotFoundException;
import com.vti.lab7.model.Position;
import com.vti.lab7.service.impl.PositionServiceImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/positions")
public class PositionController {
	private final PositionServiceImpl positionServiceImpl;
	
	@GetMapping()
	public ResponseEntity<RestData<List<PositionDTO>>> getAllPosition(){
		List<Position> positions = positionServiceImpl.findAll();
		List<PositionDTO> positionsDTO = positions.stream().map(PositionMapperDTO::convertPositionDTO).toList();
		RestData<List<PositionDTO>> restData = new RestData<>();
		restData.setData(positionsDTO);
		restData.setError(null);
		restData.setMessage("get position success");
		restData.setStatus(200);
		return ResponseEntity.ok(restData);
		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<RestData<PositionDTO>> getPositionByID(@PathVariable long id) throws  MethodArgumentTypeMismatchException{
		Position position = positionServiceImpl.findById(id);
		if(position == null)
			throw new NotFoundException("Id invalid");
		RestData<PositionDTO> restData = new RestData<>();
		restData.setData(PositionMapperDTO.convertPositionDTO(position));
		restData.setError("null");
		restData.setMessage("get position by id = "+id+" success");
		restData.setStatus(200);
		return ResponseEntity.ok(restData);
		
	}
	
	@DeleteMapping("/{id}")
    public ResponseEntity<RestData<Void>> deletePosition(@PathVariable Long id) throws  MethodArgumentTypeMismatchException {
		Position position = positionServiceImpl.findById(id);
		if(position == null)
			throw new NotFoundException("Id invalid");
		positionServiceImpl.deleteById(id);
        RestData<Void> restData = new RestData<>();
        restData.setData(null);
        restData.setError(null);
        restData.setMessage("delete position success");
        restData.setStatus(200);
        return ResponseEntity.ok(restData);
    }
	
	@PostMapping
    public ResponseEntity<RestData<PositionDTO>> createPosition(@RequestBody PositionRequestDTO request) {	
		System.out.println(request.getPositionName());
        Position savedPosition = positionServiceImpl.createPosition(request.getPositionName());
        PositionDTO savedPositionDTO = PositionMapperDTO.convertPositionDTO(savedPosition);
        RestData<PositionDTO> restData = new RestData<>();
        restData.setData(savedPositionDTO);
        restData.setError(null);
        restData.setMessage("create position success");
        restData.setStatus(201);
        return ResponseEntity.status(HttpStatus.CREATED).body(restData);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestData<PositionDTO>> updatePosition(@PathVariable Long id, @RequestBody PositionRequestDTO request) throws MethodArgumentTypeMismatchException {
    	Position position = positionServiceImpl.findById(id);
		if(position == null)
			throw new NotFoundException("Id invalid");
		position.setPositionName(request.getPositionName());
        Position updatedPosition = positionServiceImpl.updatePosition(position);
        PositionDTO updatedPositionDTO = PositionMapperDTO.convertPositionDTO(updatedPosition);
        RestData<PositionDTO> restData = new RestData<>();
        restData.setData(updatedPositionDTO);
        restData.setError(null);
        restData.setMessage("update position success");
        restData.setStatus(200);
        return ResponseEntity.ok(restData);
    }
	
	
	 
	
	
}
