package com.erp.controllers;

import com.erp.enities.Department;
import com.erp.services.DepartmentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments(){
        return ResponseEntity.ok(departmentService.getAllDept());
    }

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department){
        return ResponseEntity.ok(departmentService.createDept(department));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable Long id,
            @RequestBody Department department
    ) {
        return ResponseEntity.ok(departmentService.updateDept(id, department));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDept(id);
        return ResponseEntity.ok("Department deleted successfully");
    }

}
