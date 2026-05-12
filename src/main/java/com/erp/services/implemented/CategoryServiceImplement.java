package com.erp.services.implemented;

import com.erp.dto.ProductCategoryDTO;
import com.erp.enities.ProductCategory;
import com.erp.enities.Supplier;
import com.erp.repositories.ProductCategoryRepository;
import com.erp.services.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImplement implements CategoryService {
    private final ProductCategoryRepository repo;

    @Override
    @Transactional
    public ProductCategory createCategory(ProductCategory category) {
        return repo.save(category);
    }

    @Override
    public List<ProductCategory> getAllCategory() {
        return repo.findAll();
    }

    @Override
    public ProductCategory updateCategory(Long id, ProductCategoryDTO productCategoryDTO) {
        ProductCategory category = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(productCategoryDTO.getName());
        category.setDescription(productCategoryDTO.getDescription());
        category.setStatus(productCategoryDTO.getStatus());


        return repo.save(category);
    }

    @Override
    public void deleteCategory(long id) {
        repo.deleteById(id);
    }
}
