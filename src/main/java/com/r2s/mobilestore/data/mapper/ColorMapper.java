package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.ColorDTO;
import com.r2s.mobilestore.data.dto.ColorProductDTO;
import com.r2s.mobilestore.data.entity.Color;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
@Component
public interface ColorMapper {


    Color toEntity(ColorDTO colorDTO);

    @Mapping(source = "name" , target = "name")
    @Mapping(source = "status" , target = "status")
    @Mapping(source = "id" , target = "id")
    @Mapping(source = "product_id" , target = "product.id")
    Color toColorEntity(ColorProductDTO colorProductDTO);

    ColorDTO toDTO(Color color);

    @Mapping(source = "name" , target = "name")
    @Mapping(source = "status" , target = "status")
    @Mapping(source = "id" , target = "id")
    @Mapping(source = "product.id" , target = "product_id")
    ColorProductDTO toColorDTO(Color color);
}
