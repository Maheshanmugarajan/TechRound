package com.mbank.restapi;

import com.mbank.restapi.dto.ExternalUserDto;
import com.mbank.restapi.exception.ResourceNotFoundException;
import com.mbank.restapi.model.Employee;
import com.mbank.restapi.repository.EmployeeRepository;
import com.mbank.restapi.service.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeServiceImplTests {

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RestTemplate restTemplate;

    // We'll set this field manually
    private String externalUserApiUrl = "http://fakeapi/users/{userId}";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService.externalUserApiUrl = externalUserApiUrl;
    }

    @Test
    void testCreateEmployee() {
        Employee emp = new Employee();
        emp.setName("John");
        when(employeeRepository.save(emp)).thenReturn(emp);
        Employee result = employeeService.createEmployee(emp);
        assertEquals(emp, result);
        verify(employeeRepository).save(emp);
    }

    @Test
    void testUpdateEmployee_Success() {
        Long id = 1L;
        Employee existing = new Employee();
        existing.setId(id);
        existing.setName("Old Name");
        existing.setEmail("old@example.com");
        existing.setDepartment("Old Dept");
        existing.setSalary(BigDecimal.TEN);

        Employee updatedData = new Employee();
        updatedData.setName("New Name");
        updatedData.setEmail("new@example.com");
        updatedData.setDepartment("New Dept");
        updatedData.setSalary(BigDecimal.valueOf(20));

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(existing)).thenReturn(existing);

        Employee result = employeeService.updateEmployee(id, updatedData);

        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("New Dept", result.getDepartment());
        assertEquals(BigDecimal.valueOf(20), result.getSalary());

        verify(employeeRepository).findById(id);
        verify(employeeRepository).save(existing);
    }

    @Test
    void testUpdateEmployee_NotFound() {
        Long id = 1L;
        Employee updatedData = new Employee();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.updateEmployee(id, updatedData));
        assertEquals("Employee not found with ID: " + id, ex.getMessage());
    }

    @Test
    void testGetEmployeeById_Success() {
        Long id = 1L;
        Employee emp = new Employee();
        emp.setId(id);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(emp));
        Employee result = employeeService.getEmployeeById(id);
        assertEquals(emp, result);
    }

    @Test
    void testGetEmployeeById_NotFound() {
        Long id = 1L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(id));
        assertEquals("Employee not found with ID: " + id, ex.getMessage());
    }

    @Test
    void testGetAllEmployees() {
        Pageable pageable = PageRequest.of(0, 10);
        Employee emp1 = new Employee();
        Employee emp2 = new Employee();
        Page<Employee> page = new PageImpl<>(List.of(emp1, emp2));

        when(employeeRepository.findAll(pageable)).thenReturn(page);
        Page<Employee> result = employeeService.getAllEmployees(pageable);

        assertEquals(2, result.getContent().size());
        verify(employeeRepository).findAll(pageable);
    }
}