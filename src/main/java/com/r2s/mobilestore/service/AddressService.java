package com.r2s.mobilestore.service;

import com.r2s.mobilestore.data.dto.address.AddressDTO;
import com.r2s.mobilestore.data.dto.address.AddressShowDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO);

    AddressDTO updateAddress(AddressDTO addressDTO,long id);

    boolean deleteAddress(long id);

    AddressDTO getById(Long id);

    List<AddressShowDTO> getAllAddress();
}
