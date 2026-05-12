package com.erp.controllers;

import com.erp.dto.EmployeeDTO;
import com.erp.enities.Employee;
import com.erp.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // GET ALL EMPLOYEES
    @PreAuthorize("hasAnyAuthority('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }


    // GET EMPLOYEE BY ID
    @PreAuthorize("hasAnyAuthority('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }


    // ADMIN CREATES HR
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/hr")
    public ResponseEntity<Employee> createHr(
            @RequestBody EmployeeDTO dto
    ) {
        return ResponseEntity.ok(employeeService.createHr(dto));
    }


    // ADMIN + HR CREATE EMPLOYEE
    @PreAuthorize("hasAnyAuthority('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<Employee> createEmployee(
            @RequestBody EmployeeDTO dto
    ) {
        return ResponseEntity.ok(employeeService.createEmployee(dto));
    }


    // UPDATE EMPLOYEE
    @PreAuthorize("hasAnyAuthority('ADMIN','HR')")
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeDTO dto
    ) {
        return ResponseEntity.ok(
                employeeService.updateEmployee(id, dto)
        );
    }


    // DELETE EMPLOYEE
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id
    ) {

        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }
}
