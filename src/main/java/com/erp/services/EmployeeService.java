package com.erp.services;

import com.erp.dto.EmployeeDTO;
import com.erp.enities.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee createEmployee(EmployeeDTO dto);
    Employee updateEmployee(Long id, EmployeeDTO dto);
    void deleteEmployee(Long id);
    Employee getEmployeeById(Long id);

    Employee createHr(EmployeeDTO dto);
}
