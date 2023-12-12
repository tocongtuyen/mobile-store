package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.StatusDTO;
import com.r2s.mobilestore.data.entity.Status;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface StatusMapper {

    Status toEntity(StatusDTO statusDTO);

    StatusDTO toDTO(Status status);
}
