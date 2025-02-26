package com.vti.lab7.controller;

import java.util.List;

 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.vti.lab7.exception.custom.BadRequestException;
import com.vti.lab7.exception.custom.NotFoundException;
import com.vti.lab7.model.Employee;
import com.vti.lab7.model.Position;
import com.vti.lab7.repository.EmployeeRepository;
import com.vti.lab7.service.impl.EmployeeServiceImpl;
import com.vti.lab7.service.impl.PositionServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/positions")
@Tag(name = "Position Controller", description = "APIs for managing positions")
public class PositionController {
	private final PositionServiceImpl positionServiceImpl;
	private final EmployeeRepository employeeRepository;
	
	@GetMapping()
	@Operation(summary = "Get all positions")
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
	@Operation(summary = "Get position by ID")
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
	@PreAuthorize("hasAuthority('position_delete_by_id')")
	@Operation(summary = "Delete position by ID")
    public ResponseEntity<RestData<Void>> deletePosition(@PathVariable Long id) throws  MethodArgumentTypeMismatchException {
		Position position = positionServiceImpl.findById(id);
		if(position == null)
			throw new NotFoundException("Id invalid");
		List<Employee> employees = employeeRepository.findByPositionPositionId(id);
		System.out.println(">>>>>>>"+employees);
		for (Employee e : employees) {
		    e.setPosition(null);
		}
		employeeRepository.saveAll(employees);

		positionServiceImpl.deleteById(id);
        RestData<Void> restData = new RestData<>();
        restData.setData(null);
        restData.setError(null);
        restData.setMessage("delete position success");
        restData.setStatus(200);
        return ResponseEntity.ok(restData);
    }
	
	@PostMapping
	@PreAuthorize("hasAuthority('position_create')")
	@Operation(summary = "Create position")
    public ResponseEntity<RestData<PositionDTO>> createPosition(@Valid @RequestBody PositionRequestDTO request)  {	
        String positionName = request.getPositionName();
        boolean isExisted = positionServiceImpl.findAll().stream().anyMatch(x -> x.getPositionName().trim().equals(positionName));
        if(isExisted == true) {
        	throw new BadRequestException("position.exist",positionName);
        }
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
    @PreAuthorize("hasAuthority('position_update_by_id')")
    @Operation(summary = "Update position by ID")
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
