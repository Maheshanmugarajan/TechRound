package com.mbank.restapi.controller;

import com.mbank.restapi.dto.EmployeeRequestDto;
import com.mbank.restapi.mapper.EmployeeMapper;
import com.mbank.restapi.model.Employee;
import com.mbank.restapi.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeMapper employeeMapper;

    // Create Employee
    @PostMapping
    @Transactional
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeRequestDto dto) {
        Employee employee = employeeMapper.toEntity(dto);
        Employee savedEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.ok(savedEmployee);
    }

    // Create Employee From Third Party Source API
    @PostMapping("/external/{id}")
    @Transactional
    public ResponseEntity<Employee> createExternalEmployee(@PathVariable Long id) {
        Employee savedEmployee = employeeService.createExternalEmployee(id);
        return ResponseEntity.ok(savedEmployee);
    }

    // Update Employee
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequestDto dto) {
        Employee updatedEmployee = employeeMapper.toEntity(dto);
        Employee employee = employeeService.updateEmployee(id, updatedEmployee);
        return ResponseEntity.ok(employee);
    }

    // Get Employee by ID
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    // Get Employee by pages
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Page<Employee>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeService.getAllEmployees(pageable);

        return ResponseEntity.ok(employeePage);
    }
}