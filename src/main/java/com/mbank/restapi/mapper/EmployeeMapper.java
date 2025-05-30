package com.mbank.restapi.mapper;

import com.mbank.restapi.dto.EmployeeRequestDto;
import com.mbank.restapi.model.Employee;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequestDto dto) {
        if (dto == null) return null;

        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(dto.getDepartment());
        employee.setSalary(dto.getSalary() != null ? dto.getSalary() : BigDecimal.ZERO);
        return employee;
    }

    public EmployeeRequestDto toDto(Employee employee) {
        if (employee == null) return null;

        EmployeeRequestDto dto = new EmployeeRequestDto();
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setDepartment(employee.getDepartment());
        dto.setSalary(employee.getSalary());
        return dto;
    }
}