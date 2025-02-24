package com.vti.lab7.service.impl;

import java.util.List;
import java.util.Optional;

import com.vti.lab7.exception.custom.BadRequestException;
import com.vti.lab7.exception.custom.ForbiddenException;
import com.vti.lab7.exception.custom.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vti.lab7.constant.ErrorMessage;
import com.vti.lab7.dto.EmployeeDTO;
import com.vti.lab7.dto.mapper.EmployeeMapper;
import com.vti.lab7.dto.response.PaginationResponseDto;
import com.vti.lab7.dto.response.PagingMeta;
import com.vti.lab7.model.Employee;
import com.vti.lab7.model.*;
import com.vti.lab7.repository.DepartmentRepository;
import com.vti.lab7.repository.EmployeeRepository;
import com.vti.lab7.repository.PositionRepository;
import com.vti.lab7.repository.UserRepository;
import com.vti.lab7.service.EmployeeService;
import com.vti.lab7.specification.EmployeeSpecification;

import lombok.RequiredArgsConstructor;
import static com.vti.lab7.constant.RoleConstants.*;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

	private final EmployeeRepository employeeRepository;

	private final UserRepository userRepository;

	private final DepartmentRepository departmentRepository;

	private final PositionRepository positionRepository;

	private Employee getEntity(long id) {
		return employeeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND_ID, id));
	}

	private User getCurrentUser() {
		String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByUsername(currentUsername)
				.orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_USERNAME, currentUsername));
	}

	@Override
	public PaginationResponseDto<EmployeeDTO> getAllEmployees(String firstName, String lastName, String phoneNumber,
			String status, Pageable pageable) {

		// Lấy thông tin người dùng hiện tại
		User currentUser = getCurrentUser();
		String roleName = currentUser.getRole().getRoleName();

		// Chỉ cho phép admin và manager truy cập
		if (!ADMIN.equals(roleName) && !MANAGER.equals(roleName)) {
			throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
		}

		// Kiểm tra nếu là MANAGER thì chỉ được lấy theo phòng ban hiện tại
		Long departmentId = null;
		if (MANAGER.equals(roleName)) {
			departmentId = currentUser.getEmployee().getDepartment().getDepartmentId();
		}

		// Tạo truy vấn
		Specification<Employee> spec = Specification.where(EmployeeSpecification.hasFirstName(firstName))
				.and(EmployeeSpecification.hasLastName(lastName)).and(EmployeeSpecification.hasPhoneNumber(phoneNumber))
				.and(EmployeeSpecification.hasStatus(status)
						.and(EmployeeSpecification.belongsToDepartment(departmentId)));

		Page<Employee> page = employeeRepository.findAll(spec, pageable);

		Sort.Order order = pageable.getSort().stream().findFirst().orElse(null);
		String sortBy = null;
		String sortType = null;
		if (order != null) {
			sortBy = order.getProperty();
			sortType = order.getDirection().name();
		}

		PagingMeta pagingMeta = new PagingMeta(page.getTotalElements(), page.getTotalPages(), pageable.getPageNumber(),
				pageable.getPageSize(), sortBy, sortType);

		List<EmployeeDTO> items = page.getContent().stream().map(EmployeeMapper::convertToDTO).toList();

		PaginationResponseDto<EmployeeDTO> responseDto = new PaginationResponseDto<>();
		responseDto.setItems(items);
		responseDto.setMeta(pagingMeta);

		return responseDto;
	}

	@Override
	public EmployeeDTO getEmployeeById(Long id) {
		// Lấy thông tin người dùng hiện tại
		User currentUser = getCurrentUser();
		String roleName = currentUser.getRole().getRoleName();

		// Admin có thể xem thông tin của toàn bộ id
		if (ADMIN.equals(roleName)) {
			return EmployeeMapper.convertToDTO(getEntity(id));
		}

		// Manager được xem thông tin trong cùng phòng ban
		if (MANAGER.equals(roleName)) {
			Employee employee = getEntity(id);

			Long managerDepartmentId = currentUser.getEmployee().getDepartment().getDepartmentId();
			if (managerDepartmentId != employee.getDepartment().getDepartmentId()) {
				throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
			}

			return EmployeeMapper.convertToDTO(employee);
		}

		// Employee chỉ được xem thông tin của bản thân
		if (EMPLOYEE.equals(roleName)) {
			if (!id.equals(currentUser.getEmployee().getEmployeeId())) {
				throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
			}
			return EmployeeMapper.convertToDTO(currentUser.getEmployee());
		}

		throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
	}

	@Override
	public EmployeeDTO createEmployee(EmployeeDTO requestDTO) {
		// Lấy thông tin người dùng hiện tại
		User currentUser = getCurrentUser();
		String roleName = currentUser.getRole().getRoleName();

		// Chỉ cho phép admin và manager truy cập
		if (!ADMIN.equals(roleName) && !MANAGER.equals(roleName)) {
			throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
		}

		// MANAGER chỉ được tạo nhân viên trong phòng ban của mình
		if (MANAGER.equals(roleName)) {
			Long requestedDepartmentId = requestDTO.getDepartmentId();
			Long managerDepartmentId = currentUser.getEmployee().getDepartment().getDepartmentId();
			if (!managerDepartmentId.equals(requestedDepartmentId)) {
				throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
			}
		}

		User user = userRepository.findById(requestDTO.getUserId())
				.orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, requestDTO.getUserId()));

		if (user.getEmployee() != null) {
			throw new BadRequestException(ErrorMessage.User.ERR_USER_ALREADY_ASSIGNED, requestDTO.getUserId());
		}

		Position position = positionRepository.findById(requestDTO.getPositionId()).orElseThrow(
				() -> new NotFoundException(ErrorMessage.Position.ERR_NOT_FOUND_ID, requestDTO.getPositionId()));

		Department department = departmentRepository.findById(requestDTO.getDepartmentId()).orElseThrow(
				() -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND_ID, requestDTO.getDepartmentId()));

		Employee newEmployee = EmployeeMapper.convertToEntity(requestDTO);
		newEmployee.setEmployeeId(null);
		newEmployee.setUser(user);
		newEmployee.setPosition(position);
		newEmployee.setDepartment(department);

		employeeRepository.save(newEmployee);

		return EmployeeMapper.convertToDTO(newEmployee);
	}

	@Override
	public EmployeeDTO updateEmployee(Long id, EmployeeDTO requestDTO) {
		// Lấy thông tin người dùng hiện tại
		User currentUser = getCurrentUser();
		String roleName = currentUser.getRole().getRoleName();

		Employee existingEmployee = getEntity(id);

		switch (roleName) {
		case ADMIN -> {
			// ADMIN có toàn quyền, không cần kiểm tra thêm
		}
		case MANAGER -> {
			// MANAGER chỉ được cập nhật nhân viên trong cùng phòng ban
			long managerDepartmentId = currentUser.getEmployee().getDepartment().getDepartmentId();
			if (existingEmployee.getDepartment().getDepartmentId() != managerDepartmentId) {
				throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
			}
		}
		case EMPLOYEE -> {
			// EMPLOYEE chỉ được cập nhật thông tin của chính mình
			if (existingEmployee.getUser().getUserId() != currentUser.getUserId()) {
				throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
			}
		}
		default -> throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
		}

		if (requestDTO.getUserId() != null) {
			if (!requestDTO.getUserId().equals(existingEmployee.getUser().getUserId())) {
				User user = userRepository.findById(requestDTO.getUserId()).orElseThrow(
						() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, requestDTO.getUserId()));

				if (user.getEmployee() != null) {
					throw new BadRequestException(ErrorMessage.User.ERR_USER_ALREADY_ASSIGNED, requestDTO.getUserId());
				}

				existingEmployee.setUser(user);
			}
		} else {
			existingEmployee.setUser(null);
		}

		if (requestDTO.getPositionId() != null) {
			if (!requestDTO.getPositionId().equals(existingEmployee.getPosition().getPositionId())) {
				Position position = positionRepository.findById(requestDTO.getPositionId())
						.orElseThrow(() -> new NotFoundException(ErrorMessage.Position.ERR_NOT_FOUND_ID,
								requestDTO.getPositionId()));

				existingEmployee.setPosition(position);
			}
		} else {
			existingEmployee.setPosition(null);
		}

		if (requestDTO.getDepartmentId() != null) {
			if (!requestDTO.getDepartmentId().equals(existingEmployee.getDepartment().getDepartmentId())) {
				Department department = departmentRepository.findById(requestDTO.getDepartmentId())
						.orElseThrow(() -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND_ID,
								requestDTO.getDepartmentId()));

				existingEmployee.setDepartment(department);
			}
		} else {
			existingEmployee.setDepartment(null);
		}

		existingEmployee.setFirstName(requestDTO.getFirstName());
		existingEmployee.setLastName(requestDTO.getLastName());
		existingEmployee.setDateOfBirth(requestDTO.getDateOfBirth());
		existingEmployee.setPhoneNumber(requestDTO.getPhoneNumber());
		existingEmployee.setAddress(requestDTO.getAddress());
		existingEmployee.setHireDate(requestDTO.getHireDate());
		existingEmployee.setSalary(requestDTO.getSalary());
		existingEmployee.setStatus(requestDTO.getStatus());

		Employee updatedEmployee = employeeRepository.save(existingEmployee);

		return EmployeeMapper.convertToDTO(updatedEmployee);
	}

	@Override
	public void deleteEmployee(Long id) {
		if (!employeeRepository.existsById(id)) {
			throw new NotFoundException(ErrorMessage.Position.ERR_NOT_FOUND_ID, id);
		}
		employeeRepository.deleteById(id);
	}

	@Override
	public List<EmployeeDTO> getEmployeesByDepartment(Long departmentId) {
		return employeeRepository.findByDepartmentDepartmentId(departmentId).stream().map(EmployeeMapper::convertToDTO)
				.toList();
	}

	@Override
	public List<EmployeeDTO> getEmployeesByPosition(Long positionId) {
		return employeeRepository.findByPositionPositionId(positionId).stream().map(EmployeeMapper::convertToDTO)
				.toList();
	}

	
	@Override
	public EmployeeDTO getEmployeeByUserId(Long userId) {
		Employee employee = employeeRepository.findByUserUserId(userId).orElse(null);
		return employee != null ? EmployeeMapper.convertToDTO(employee) : null;
	}
}




