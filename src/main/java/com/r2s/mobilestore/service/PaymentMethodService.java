package com.r2s.mobilestore.service;

import com.r2s.mobilestore.common.MessageResponse;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.data.dto.ProductTechDTO;
import com.r2s.mobilestore.data.entity.PaymentMethod;

import java.util.List;

public interface PaymentMethodService {
    MessageResponse create(PaymentMethodDTO paymentMethodDTO);

    PaymentMethodDTO update(long id,PaymentMethodDTO paymentMethodDTO);

    boolean deleteById(long id);
    
    PaymentMethodDTO getById(long id);
    
    PaginationDTO getAllPagination(int no, int limit);
}
