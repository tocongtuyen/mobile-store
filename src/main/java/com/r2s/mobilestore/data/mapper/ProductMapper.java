package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.*;
import com.r2s.mobilestore.data.dto.product.ProductDTO;
import com.r2s.mobilestore.data.dto.product.ShowProductDTO;
import com.r2s.mobilestore.data.entity.*;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;


@Mapper(componentModel = "spring", uses = {CategoriesMapper.class, ManufacturerMapper.class})
@Component
public interface ProductMapper {

    @Mapping(ignore = true, target = "category")
    @Mapping(ignore = true, target = "manufacturer")
    Product toEntity(ProductDTO productDTO);

    @Mapping(source = "category", target = "categoriesDTO")
    @Mapping(source = "manufacturer", target = "manufacturerDTO")
    @Mapping(source = "productTechs", target = "productTechDTOs")
    @Mapping(source = "series", target = "seriDTOs")
    @Mapping(source = "colors", target = "colorDTOs")
    @Mapping(source = "memories", target = "memoryDTOs")
    @Mapping(source = "reviews", target = "reviewDTOs")
    @Mapping(source = "images", target = "imageDTOs")
    ProductDTO toDTO(Product product);


    @Mapping(source = "category", target = "categoriesDTO")
    @Mapping(source = "manufacturer", target = "manufacturerDTO")
    @Mapping(source = "images", target = "imageDTOs")
    ShowProductDTO toShowProductDTO(Product product);


}
