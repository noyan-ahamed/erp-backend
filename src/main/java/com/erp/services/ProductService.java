package com.erp.services;

import com.erp.dto.ProductDTO;
import com.erp.enities.Product;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    List<Product> getAllProduct();

    ResponseEntity<Product> createProduct(Product product);

    ResponseEntity<Product> updateProduct(ProductDTO productDTO);

    void deleteProduct(long id);
    Product updateProduct(Long id, ProductDTO dto);
}