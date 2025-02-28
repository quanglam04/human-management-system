package com.vti.lab7.service.impl;

import com.vti.lab7.config.CustomUserDetails;
import com.vti.lab7.constant.ErrorMessage;
import com.vti.lab7.dto.EmployeeDTO;
import com.vti.lab7.dto.mapper.EmployeeMapper;
import com.vti.lab7.dto.response.PaginationResponseDto;
import com.vti.lab7.dto.response.PagingMeta;
import com.vti.lab7.exception.custom.BadRequestException;
import com.vti.lab7.exception.custom.ForbiddenException;
import com.vti.lab7.exception.custom.NotFoundException;
import com.vti.lab7.model.Department;
import com.vti.lab7.model.Employee;
import com.vti.lab7.model.Position;
import com.vti.lab7.model.User;
import com.vti.lab7.repository.DepartmentRepository;
import com.vti.lab7.repository.EmployeeRepository;
import com.vti.lab7.repository.PositionRepository;
import com.vti.lab7.repository.UserRepository;
import com.vti.lab7.service.EmployeeService;
import com.vti.lab7.specification.EmployeeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static com.vti.lab7.constant.RoleConstants.*;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;

    private final PositionRepository positionRepository;

    private final Random random = new Random();

    private Employee getEntity(long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND_ID, id));
    }

    private PaginationResponseDto<EmployeeDTO> fetchEmployeesByFilters(String firstName, String lastName, String phoneNumber, String status, Long departmentId, Pageable pageable) {
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

    private Date generateRandomDate(int yearStart, int yearEnd) {
        int day = random.nextInt(28) + 1;
        int month = random.nextInt(12) + 1;
        int year = random.nextInt(yearEnd - yearStart + 1) + yearStart;
        return Date.valueOf(LocalDate.of(year, month, day));
    }

    @Override
    public void init() {
        if (employeeRepository.count() == 0) {
            List<User> users = userRepository.findAll();
            List<Position> positions = positionRepository.findAll();
            List<Department> departments = departmentRepository.findAll();

            if (users.isEmpty() || positions.isEmpty() || departments.isEmpty()) {
                System.out.println("Thiếu dữ liệu User, Position hoặc Department để khởi tạo Employee.");
                return;
            }

            String[] firstNames = {"Nguyen", "Tran", "Le", "Pham", "Hoang", "Phan", "Vu", "Vo", "Dang", "Bui"};
            String[] lastNames = {"An", "Binh", "Cuong", "Dung", "Em", "Phong", "Quan", "Son", "Tien", "Viet"};

            for (int i = 0; i < 20; i++) {
                Employee employee = new Employee();

                employee.setFirstName(firstNames[random.nextInt(firstNames.length)]);
                employee.setLastName(lastNames[random.nextInt(lastNames.length)]);

                employee.setDateOfBirth(generateRandomDate(1980, 2000));
                employee.setPhoneNumber("09" + (10000000 + random.nextInt(90000000)));
                employee.setAddress("Địa chỉ ngẫu nhiên " + i);
                employee.setHireDate(generateRandomDate(2015, 2023));
                employee.setSalary(BigDecimal.valueOf(500 + random.nextInt(5000)));
                employee.setStatus(random.nextBoolean() ? "ACTIVE" : "INACTIVE");

                employee.setUser(users.get(i));
                employee.setPosition(positions.get(random.nextInt(positions.size())));
                employee.setDepartment(departments.get(random.nextInt(departments.size())));

                employeeRepository.save(employee);
            }
        }
    }

    @Override
    public PaginationResponseDto<EmployeeDTO> getAllEmployees(String firstName, String lastName, String phoneNumber, String status, Pageable pageable, CustomUserDetails userDetails) {
        switch (userDetails.getRoleName()) {
            case ADMIN -> {
                return fetchEmployeesByFilters(firstName, lastName, phoneNumber, status, null, pageable);
            }

            case MANAGER -> {
                Long departmentId = departmentRepository.findDepartmentIdByUsername(userDetails.getUsername());
                if (departmentId == null) {
                    throw new BadRequestException(ErrorMessage.Employee.ERR_MANAGER_NO_DEPARTMENT, userDetails.getUsername());
                }
                return fetchEmployeesByFilters(firstName, lastName, phoneNumber, status, departmentId, pageable);
            }

            default -> throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
        }
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id, CustomUserDetails userDetails) {
        switch (userDetails.getRoleName()) {
            case ADMIN -> {
                return EmployeeMapper.convertToDTO(getEntity(id));
            }

            case MANAGER -> {
                Employee employee = getEntity(id);
                if (employee.getDepartment() == null) {
                    throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
                }

                Long departmentId = departmentRepository.findDepartmentIdByUsername(userDetails.getUsername());
                if (departmentId == null) {
                    throw new BadRequestException(ErrorMessage.Employee.ERR_MANAGER_NO_DEPARTMENT, userDetails.getUsername());
                }

                if (!departmentId.equals(employee.getDepartment().getDepartmentId())) {
                    throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
                }

                return EmployeeMapper.convertToDTO(employee);
            }

            case EMPLOYEE -> {
                Long employeeId = employeeRepository.findEmployeeIdByUsername(userDetails.getUsername());
                if (employeeId == null) {
                    throw new BadRequestException(ErrorMessage.Employee.ERR_USER_NO_EMPLOYEE, userDetails.getUsername());
                }
                if (!employeeId.equals(id)) {
                    throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
                }
                return EmployeeMapper.convertToDTO(getEntity(id));
            }

            default -> throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
        }
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO requestDTO, CustomUserDetails userDetails) {
        switch (userDetails.getRoleName()) {
            case ADMIN -> {

            }

            case MANAGER -> {
                Long departmentId = departmentRepository.findDepartmentIdByUsername(userDetails.getUsername());
                if (departmentId == null) {
                    throw new BadRequestException(ErrorMessage.Employee.ERR_MANAGER_NO_DEPARTMENT, userDetails.getUsername());
                }

                if (!departmentId.equals(requestDTO.getDepartmentId())) {
                    throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
                }
            }

            default -> throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
        }

        //Kiểm tra người dùng
        User user = null;
        if (requestDTO.getUserId() != null) {
            user = userRepository.findById(requestDTO.getUserId()).orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, requestDTO.getUserId()));
            if (user.getEmployee() != null) {
                throw new BadRequestException(ErrorMessage.User.ERR_USER_ALREADY_ASSIGNED, requestDTO.getUserId());
            }
        }

        //Kiểm tra vị trí
        Position position = null;
        if (requestDTO.getPositionId() != null) {
            position = positionRepository.findById(requestDTO.getPositionId()).orElseThrow(() -> new NotFoundException(ErrorMessage.Position.ERR_NOT_FOUND_ID, requestDTO.getPositionId()));
        }

        //Kiểm tra phòng ban
        Department department = null;
        if (requestDTO.getDepartmentId() != null) {
            department = departmentRepository.findById(requestDTO.getDepartmentId()).orElseThrow(() -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND_ID, requestDTO.getDepartmentId()));
        }

        //Tạo mới nhân viên
        Employee newEmployee = EmployeeMapper.convertToEntity(requestDTO);
        newEmployee.setEmployeeId(null);
        newEmployee.setUser(user);
        newEmployee.setPosition(position);
        newEmployee.setDepartment(department);

        employeeRepository.save(newEmployee);

        return EmployeeMapper.convertToDTO(newEmployee);
    }

    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO requestDTO, CustomUserDetails userDetails) {
        Employee existingEmployee = getEntity(id);

        switch (userDetails.getRoleName()) {
            case ADMIN -> {
            }

            case MANAGER -> {
                if (existingEmployee.getDepartment() == null) {
                    throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
                }

                Long departmentId = departmentRepository.findDepartmentIdByUsername(userDetails.getUsername());
                if (departmentId == null) {
                    throw new BadRequestException(ErrorMessage.Employee.ERR_MANAGER_NO_DEPARTMENT, userDetails.getUsername());
                }

                if (!departmentId.equals(existingEmployee.getDepartment().getDepartmentId())) {
                    throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
                }
            }

            case EMPLOYEE -> {
                Long employeeId = employeeRepository.findEmployeeIdByUsername(userDetails.getUsername());
                if (employeeId == null) {
                    throw new BadRequestException(ErrorMessage.Employee.ERR_USER_NO_EMPLOYEE, userDetails.getUsername());
                }
                if (!employeeId.equals(id)) {
                    throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
                }
            }

            default -> throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
        }

        //Kiểm tra người dùng
        User user = null;
        if (requestDTO.getUserId() != null) {
            user = userRepository.findById(requestDTO.getUserId()).orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, requestDTO.getUserId()));
            if (user.getEmployee() != null) {
                throw new BadRequestException(ErrorMessage.User.ERR_USER_ALREADY_ASSIGNED, requestDTO.getUserId());
            }
        }

        //Kiểm tra vị trí
        Position position = null;
        if (requestDTO.getPositionId() != null) {
            position = positionRepository.findById(requestDTO.getPositionId()).orElseThrow(() -> new NotFoundException(ErrorMessage.Position.ERR_NOT_FOUND_ID, requestDTO.getPositionId()));
        }

        //Kiểm tra phòng ban
        Department department = null;
        if (requestDTO.getDepartmentId() != null) {
            department = departmentRepository.findById(requestDTO.getDepartmentId()).orElseThrow(() -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND_ID, requestDTO.getDepartmentId()));
        }

        existingEmployee.setFirstName(requestDTO.getFirstName());
        existingEmployee.setLastName(requestDTO.getLastName());
        existingEmployee.setDateOfBirth(requestDTO.getDateOfBirth());
        existingEmployee.setPhoneNumber(requestDTO.getPhoneNumber());
        existingEmployee.setAddress(requestDTO.getAddress());
        existingEmployee.setHireDate(requestDTO.getHireDate());
        existingEmployee.setSalary(requestDTO.getSalary());
        existingEmployee.setStatus(requestDTO.getStatus());
        existingEmployee.setUser(user);
        existingEmployee.setPosition(position);
        existingEmployee.setDepartment(department);

        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        return EmployeeMapper.convertToDTO(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id, CustomUserDetails userDetails) {
        if (!ADMIN.equals(userDetails.getRoleName())) {
            throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
        }

        if (!employeeRepository.existsById(id)) {
            throw new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND_ID, id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public List<EmployeeDTO> getEmployeesByDepartment(Long departmentId, CustomUserDetails userDetails) {
        if (!ADMIN.equals(userDetails.getRoleName())) {
            throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
        }
        return employeeRepository.findByDepartmentDepartmentId(departmentId).stream().map(EmployeeMapper::convertToDTO).toList();
    }

    @Override
    public List<EmployeeDTO> getEmployeesByPosition(Long positionId, CustomUserDetails userDetails) {
        if (!ADMIN.equals(userDetails.getRoleName())) {
            throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN);
        }
        return employeeRepository.findByPositionPositionId(positionId).stream().map(EmployeeMapper::convertToDTO).toList();
    }

}