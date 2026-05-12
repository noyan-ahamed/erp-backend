package com.erp.repositories;

import com.erp.enities.Employee;
import com.erp.enities.Product;
import com.erp.enities.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
//    ProductStock  findByProduct(Product product);
    Long findByProductId(long id);

    //gpt ay method diyeche
    Optional<ProductStock> findByProduct(Product product);
}
