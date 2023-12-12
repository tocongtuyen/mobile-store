package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.RoleDTO;
import com.r2s.mobilestore.data.entity.Role;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface RoleMapper {
    Role toEntity(RoleDTO roleDTO);

    RoleDTO toDTO(Role role);

}
