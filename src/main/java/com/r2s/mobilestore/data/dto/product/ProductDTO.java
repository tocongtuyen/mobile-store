package com.r2s.mobilestore.data.dto.product;


import com.r2s.mobilestore.data.dto.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author NguyenTienDat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private long id;
    private CategoriesDTO categoriesDTO;
    private ManufacturerDTO manufacturerDTO;
    private String name;
    private String description;
    private BigDecimal price;
    private int stocks;
    private boolean status;
    private int views;
    private float star;
    private List<ProductTechDTO> productTechDTOs;
    private List<SeriDTO> seriDTOs;
    private List<ColorDTO> colorDTOs;
    private List<MemoryDTO> memoryDTOs;
    private List<ReviewDTO> reviewDTOs;
    private List<ImageDTO> imageDTOs;

}
