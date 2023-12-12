package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.user.UserCreationDTO;
import com.r2s.mobilestore.data.dto.user.UserDTO;
import com.r2s.mobilestore.data.dto.user.UserProfileDTO;
import com.r2s.mobilestore.data.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = { RoleMapper.class, StatusMapper.class })
@Component
public interface UserMapper {

	@Mapping(ignore = true, target = "password")
    User toEntity(UserCreationDTO creationDTO);

	User toEntity(UserProfileDTO userProfileDTO);

	@Mapping(source = "role", target = "roleDTO")
	@Mapping(source = "status", target = "statusDTO")
	@Mapping(source = "lock_status", target = "lockStatusDTO")
	UserDTO toDTO(User user);

}
