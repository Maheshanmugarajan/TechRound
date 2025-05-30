package com.mbank.restapi.service;

import com.mbank.restapi.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Employee createEmployee(Employee employee);

    Employee createExternalEmployee(Long id);

    Employee updateEmployee(Long id, Employee employee);

    Employee getEmployeeById(Long id);

    Page<Employee> getAllEmployees(Pageable pageable);
}