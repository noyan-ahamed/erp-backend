package com.erp.controllers;

import com.erp.enities.ProductCategory;
import com.erp.dto.ProductCategoryDTO;
import com.erp.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<List<ProductCategory>> getAllProduct(){
        List<ProductCategory> category = categoryService.getAllCategory();
        return ResponseEntity.ok(category);
    }

    @PostMapping("/create-category")
    public ResponseEntity<ProductCategory> createCategory(@RequestBody ProductCategory category){
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> updateCategory(
            @PathVariable Long id,
            @RequestBody ProductCategoryDTO dto){
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
