package com.vti.lab7.service.impl;

import java.util.List;

import com.vti.lab7.exception.custom.ForbiddenException;
import com.vti.lab7.exception.custom.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

	private final EmployeeRepository employeeRepository;

	private final UserRepository userRepository;

	private final DepartmentRepository departmentRepository;

	private final PositionRepository positionRepository;

	private Employee getEntity(long id) {
		return employeeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + id));
	}

	@Override
	public PaginationResponseDto<EmployeeDTO> getAllEmployees(String firstName, String lastName, String phoneNumber,
			String status, Pageable pageable) {

        //Lấy thông tin người dùng hiện tại
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUsername = authentication.getName();
		Employee currentEmployee = employeeRepository.findByUserUsername(currentUsername).orElseThrow(() -> new NotFoundException(
                "...", currentUsername));

        //Kiểm tra nếu là MANAGER
        Long departmentId = null;
		if("MANAGER".equals(currentEmployee.getUser().getRole().getRoleName())){
              departmentId = currentEmployee.getDepartment().getDepartmentId();
		}

		Specification<Employee> spec = Specification.where(EmployeeSpecification.hasFirstName(firstName))
				.and(EmployeeSpecification.hasLastName(lastName)).and(EmployeeSpecification.hasPhoneNumber(phoneNumber))
				.and(EmployeeSpecification.hasStatus(status).and(EmployeeSpecification.belongsToDepartment(departmentId)));

		Page<Employee> page = employeeRepository.findAll(spec, pageable);

		Sort.Order order = pageable.getSort().stream().findFirst().orElse(null);
		String sortBy= null;
		String sortType = null;
		if(order != null){
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
        //Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new NotFoundException(
                "...", currentUsername));

        String roleName= currentUser.getRole().getRoleName();

        if ("ADMIN".equals(roleName)) {
            return EmployeeMapper.convertToDTO(getEntity(id));
        }

        if ("EMPLOYEE".equals(roleName)) {
            if (!id.equals(currentUser.getEmployee().getEmployeeId())) {
                throw new ForbiddenException("Bạn chỉ được phép xem thông tin của bản thân.");
            }
            return EmployeeMapper.convertToDTO(currentUser.getEmployee());
        }

        if ("MANAGER".equals(roleName)) {
            Long managerDepartmentId = currentUser.getEmployee().getDepartment().getDepartmentId();

            Employee employee = employeeRepository.findByEmployeeIdAndDepartmentDepartmentId(id, managerDepartmentId)
                    .orElseThrow(() -> new ForbiddenException("Bạn không có quyền truy cập thông tin nhân viên này."));

            return EmployeeMapper.convertToDTO(employee);
        }

        throw new ForbiddenException("Vai trò người dùng không hợp lệ.");
	}

	@Override
	public EmployeeDTO createEmployee(EmployeeDTO requestDTO) {
        // Lấy thông tin người dùng hiện tại
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng", currentUsername));

        String roleName = currentUser.getRole().getRoleName();
        Long requestedDepartmentId = requestDTO.getDepartmentId();

        // MANAGER chỉ được tạo nhân viên trong phòng ban của mình
        if ("MANAGER".equals(roleName)) {
            Long managerDepartmentId = currentUser.getEmployee().getDepartment().getDepartmentId();
            if (!managerDepartmentId.equals(requestedDepartmentId)) {
                throw new ForbiddenException("Bạn chỉ được phép tạo nhân viên trong phòng ban của mình.");
            }
        }

		User user = userRepository.findById(requestDTO.getUserId())
				.orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + requestDTO.getUserId()));

		if (user.getEmployee() != null) {
			throw new IllegalStateException(
					"User with ID " + requestDTO.getUserId() + " is already assigned to an employee.");
		}

		Position position = positionRepository.findById(requestDTO.getPositionId()).orElseThrow(
				() -> new EntityNotFoundException("Position not found with ID: " + requestDTO.getPositionId()));

		Department department = departmentRepository.findById(requestDTO.getDepartmentId()).orElseThrow(
				() -> new EntityNotFoundException("Department not found with ID: " + requestDTO.getDepartmentId()));

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
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng", currentUsername));

        String roleName = currentUser.getRole().getRoleName();

        Employee existingEmployee = getEntity(id);

        if ("ADMIN".equals(roleName)) {
            // ADMIN có toàn quyền, không cần kiểm tra thêm
        } else if ("MANAGER".equals(roleName)) {
            // MANAGER chỉ được cập nhật nhân viên trong cùng phòng ban
            long managerDepartmentId = currentUser.getEmployee().getDepartment().getDepartmentId();
            if (existingEmployee.getDepartment().getDepartmentId() != managerDepartmentId) {
                throw new ForbiddenException("Bạn chỉ được phép cập nhật nhân viên trong phòng ban của mình.");
            }
        } else if ("EMPLOYEE".equals(roleName)) {
            // EMPLOYEE chỉ được cập nhật thông tin của chính mình
            if (existingEmployee.getUser().getUserId() != currentUser.getUserId()) {
                throw new ForbiddenException("Bạn chỉ được phép cập nhật thông tin của bản thân.");
            }
        } else {
            throw new ForbiddenException("Vai trò người dùng không hợp lệ.");
        }

		if (requestDTO.getUserId() != null) {
			if (!requestDTO.getUserId().equals(existingEmployee.getUser().getUserId())) {
				User user = userRepository.findById(requestDTO.getUserId()).orElseThrow(
						() -> new EntityNotFoundException("User not found with ID: " + requestDTO.getUserId()));

				if (user.getEmployee() != null) {
					throw new IllegalStateException(
							"User with ID " + requestDTO.getUserId() + " is already assigned to an employee.");
				}

				existingEmployee.setUser(user);
			}
		} else {
			existingEmployee.setUser(null);
		}

		if (requestDTO.getPositionId() != null) {
			if (!requestDTO.getPositionId().equals(existingEmployee.getPosition().getPositionId())) {
				Position position = positionRepository.findById(requestDTO.getPositionId()).orElseThrow(
						() -> new EntityNotFoundException("Position not found with ID: " + requestDTO.getPositionId()));

				existingEmployee.setPosition(position);
			}
		} else {
			existingEmployee.setPosition(null);
		}

		if (requestDTO.getDepartmentId() != null) {
			if (!requestDTO.getDepartmentId().equals(existingEmployee.getDepartment().getDepartmentId())) {
				Department department = departmentRepository.findById(requestDTO.getDepartmentId())
						.orElseThrow(() -> new EntityNotFoundException(
								"Department not found with ID: " + requestDTO.getDepartmentId()));

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
			throw new EntityNotFoundException("Employee not found with ID: " + id);
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

}