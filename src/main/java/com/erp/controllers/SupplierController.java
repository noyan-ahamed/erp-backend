package com.erp.controllers;

import com.erp.dto.SupplierDTO;
import com.erp.enities.Supplier;
import com.erp.services.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/supplier")
@PreAuthorize("hasAuthority('ADMIN')")
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping({"","/"})
    public ResponseEntity<List<Supplier>> getAllSupplier(){
        List<Supplier> suppliers = supplierService.getAllSupliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public Optional<Supplier> getSupplierById(@PathVariable long id){
        return supplierService.getSupplierById(id);
    }

    @PostMapping("/create-supplier")
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier){
        return ResponseEntity.ok(supplierService.createSupplier(supplier));
    }

    @PutMapping("/update-supplier/{id}")
    public ResponseEntity<Supplier> updateSupplier(
            @PathVariable Long id,
            @RequestBody SupplierDTO dto) {

        return ResponseEntity.ok(supplierService.updateSupplier(id, dto));
    }


    @DeleteMapping("/delete-supplier/{id}")
    public void deleteSupplier(@PathVariable long id){
        supplierService.deleteSupplier(id);
    }


}
