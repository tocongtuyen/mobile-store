package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.TechnicalDTO;
import com.r2s.mobilestore.data.entity.Technical;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnicalMapper {

    Technical toEntity(TechnicalDTO technicalDTO);

    TechnicalDTO toDTO(Technical technical);
}
