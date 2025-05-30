package com.mbank.restapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbank.restapi.dto.ExternalUserDto;
import com.mbank.restapi.exception.ResourceNotFoundException;
import com.mbank.restapi.model.Employee;
import com.mbank.restapi.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${external.user.api.url}")
    public String externalUserApiUrl;

    @Override
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee createExternalEmployee(Long userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            logger.info("Triggering External User API [{}] for ID [{}]", externalUserApiUrl, userId);
            ResponseEntity<ExternalUserDto> response = restTemplate.exchange(externalUserApiUrl, HttpMethod.GET, requestEntity, ExternalUserDto.class, userId);
            logger.info("External User API, Response : [{}]", objectMapper.writeValueAsString(response));
            ExternalUserDto externalUser = response.getBody();

            if (externalUser == null) {
                throw new ResourceNotFoundException("User not found in external API");
            }

            Employee employee = new Employee();
            employee.setName(externalUser.getName());
            employee.setEmail(externalUser.getEmail());
            employee.setDepartment("Imported"); // optional
            employee.setSalary(BigDecimal.ZERO); // default salary

            return employeeRepository.save(employee);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Employee updateEmployee(Long id, Employee updatedData) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        existing.setName(updatedData.getName());
        existing.setEmail(updatedData.getEmail());
        existing.setDepartment(updatedData.getDepartment());
        existing.setSalary(updatedData.getSalary());

        return employeeRepository.save(existing);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    @Override
    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }
}