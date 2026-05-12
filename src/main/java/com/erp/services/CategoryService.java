package com.erp.services;

import com.erp.dto.ProductCategoryDTO;
import com.erp.dto.SupplierDTO;
import com.erp.enities.ProductCategory;
import com.erp.enities.Supplier;

import java.util.List;

public interface CategoryService {
    ProductCategory createCategory(ProductCategory category);
    List<ProductCategory> getAllCategory();
    ProductCategory updateCategory(Long id, ProductCategoryDTO productCategoryDTO);
    void deleteCategory(long id);

}
