package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.address.AddressDTO;
import com.r2s.mobilestore.data.dto.address.AddressShowDTO;
import com.r2s.mobilestore.data.entity.Address;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring", uses = { UserMapper.class})
@Component
public interface AddressMapper {
    @Mapping(source = "location", target = "location")
    @Mapping(source = "phoneReceiver", target = "phoneReceiver")
    @Mapping(source = "nameReceiver", target = "nameReceiver")
    @Mapping(source = "defaults", target = "defaults")
    AddressDTO toDTO(Address address);
    AddressShowDTO toShowDTO(Address address);

    @Mapping(source = "location", target = "location")
    @Mapping(source = "phoneReceiver", target = "phoneReceiver")
    @Mapping(source = "nameReceiver", target = "nameReceiver")
    @Mapping(source = "defaults", target = "defaults")
    Address toEnity(AddressDTO addressDTO);
}
