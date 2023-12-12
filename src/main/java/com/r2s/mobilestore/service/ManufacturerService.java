package com.r2s.mobilestore.service;

import com.r2s.mobilestore.common.MessageResponse;
import com.r2s.mobilestore.data.dto.ManufacturerDTO;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.PaymentMethodDTO;

import java.util.List;

public interface ManufacturerService {

    MessageResponse create(ManufacturerDTO manufacturerDTO);

    ManufacturerDTO update(long id,ManufacturerDTO manufacturerDTO);

    boolean deleteById(long id);

    ManufacturerDTO getById(long id);
    
    PaginationDTO getAllPagination(int no, int limit);

}
