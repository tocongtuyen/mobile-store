package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.ManufacturerDTO;
import com.r2s.mobilestore.data.entity.Manufacturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManufacturerMapper {

    @Mapping(target = "status",constant = "true")
    Manufacturer toEntity(ManufacturerDTO manufacturerDTO);

    ManufacturerDTO toDTO(Manufacturer manufacturer);
}
