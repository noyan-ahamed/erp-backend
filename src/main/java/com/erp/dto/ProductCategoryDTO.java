package com.erp.dto;

import com.erp.enities.Product;
import com.erp.enums.ProductCategoryStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductCategoryDTO {


    @NotNull(message = "Name is Required")
    private String name;

    private String description;

    private ProductCategoryStatus status;
}
