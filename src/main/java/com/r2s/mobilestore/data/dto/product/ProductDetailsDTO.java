package com.r2s.mobilestore.data.dto.product;

import com.r2s.mobilestore.data.dto.CategoriesDTO;
import com.r2s.mobilestore.data.dto.ManufacturerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailsDTO {
    private CategoriesDTO categoriesDTO;
    private ManufacturerDTO manufacturerDTO;
    private int stocks;
    private boolean status;
}
