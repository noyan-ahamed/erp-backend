package com.erp.services.implemented;

import com.erp.dto.ProductDTO;
import com.erp.enities.Product;
import com.erp.enities.ProductStock;
import com.erp.repositories.ProductRepository;
import com.erp.repositories.ProductStockRepository;
import com.erp.services.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImplement implements ProductService {
    private final ProductRepository productRepo;

    private final ProductStockRepository stockRepo;

    @Override
    public List<Product> getAllProduct() {
        return productRepo.findAll(Sort.by(Sort.Direction.ASC,"id"));
    }

    @Override
    public ResponseEntity<Product> createProduct(Product product) {

        // first product save
        Product savedProduct = productRepo.save(product);

        //then crate stock
        ProductStock stock = new ProductStock();
        stock.setProduct(savedProduct);
        stock.setQuantity(0); // default 0

        stockRepo.save(stock);

        return ResponseEntity.ok(savedProduct);
    }

    @Override
    public ResponseEntity<Product> updateProduct(ProductDTO productDTO) {
        return null;
    }

    @Override
    public void deleteProduct(long id) {
        productRepo.deleteById(id);
    }

    @Override
    @Transactional
    public Product updateProduct(
            Long id,
            ProductDTO dto
    ) {

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found")
                );

        // only name editable
        product.setName(dto.getName());

        return productRepo.save(product);
    }
}
