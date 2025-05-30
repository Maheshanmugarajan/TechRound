package com.mbank.restapi.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class EmployeeRequestDto {

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Size(max = 100)
    private String email;

    private String department;

    @DecimalMin(value = "0.0", inclusive = true, message = "Salary must be positive or zero")
    @Digits(integer = 8, fraction = 2, message = "Invalid salary format")
    private BigDecimal salary;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}