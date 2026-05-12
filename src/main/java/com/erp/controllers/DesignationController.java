package com.erp.controllers;

import com.erp.enities.Designation;
import com.erp.services.DepartmentService;
import com.erp.services.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/designation")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class DesignationController {
    private final DesignationService designationService;
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<Designation>> getAllDesignations(){
        return ResponseEntity.ok(designationService.getAllDesignations());
    }

    @PostMapping
    public ResponseEntity<Designation> createDesignation(@RequestBody Designation designation){
        return ResponseEntity.ok(designationService.createDesignation(designation));
    }

    @GetMapping("/by_dept_id/{id}")
    public ResponseEntity<List<Designation>> searchByDeptId(@PathVariable Long id){
        return ResponseEntity.ok(designationService.designationByDeptId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Designation> updateDesignation(
            @PathVariable Long id,
            @RequestBody Designation designation
    ) {
        return ResponseEntity.ok(
                designationService.updateDesignation(id, designation)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDesignation(@PathVariable Long id) {

        designationService.deleteDesignation(id);

        return ResponseEntity.ok("Designation deleted successfully");
    }
}
