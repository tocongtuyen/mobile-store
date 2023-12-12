package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.CategoriesDTO;
import com.r2s.mobilestore.data.entity.Categories;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface CategoriesMapper {

    @Mapping(target = "status",constant = "true")
    Categories toEntity(CategoriesDTO categoriesDTO);

    CategoriesDTO toDTO(Categories categories);
}
