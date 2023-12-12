package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.MemoryDTO;
import com.r2s.mobilestore.data.entity.Memory;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface MemoryMapper {

    Memory toEntity(MemoryDTO memoryDTO);

    MemoryDTO toDTO(Memory memory);
}
