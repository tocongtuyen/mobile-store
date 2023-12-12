package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.ProductTechDTO;
import com.r2s.mobilestore.data.entity.ProductTech;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TechnicalMapper.class})
public interface ProductTechMapper {

    @Mapping(ignore = true, target = "technical")
    @Mapping(target = "status",constant = "true")
    ProductTech toEntity(ProductTechDTO productTechDTO);

    @Mapping(source = "technical", target = "technicalDTO")
    ProductTechDTO toDTO(ProductTech productTech);
}
