package com.r2s.mobilestore.service;

import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.data.dto.TechnicalDTO;

/**
 * @author NguyenTienDat
 */
public interface TechnicalService {

    TechnicalDTO createTechnical(TechnicalDTO technicalDTO);

    TechnicalDTO create(TechnicalDTO technicalDTO);

    TechnicalDTO create(String name);

    TechnicalDTO update(int id, TechnicalDTO technicalDTO);
    
    TechnicalDTO getById(int id);
    
    PaginationDTO getAllPagination(int no, int limit);
}
