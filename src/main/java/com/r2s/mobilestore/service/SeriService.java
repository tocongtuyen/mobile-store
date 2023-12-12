package com.r2s.mobilestore.service;

import com.r2s.mobilestore.common.MessageResponse;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.data.dto.SeriDTO;
import com.r2s.mobilestore.data.entity.Product;
import com.r2s.mobilestore.data.dto.SeriProductDTO;

import java.util.List;

/**
 * @author NguyenTienDat
 */
public interface SeriService {


    List<SeriDTO> createProductSeri(Product product, List<SeriDTO> seriDTOs);

    List<SeriDTO> updateProductSeri(Product product, List<SeriDTO> seriDTOs);

    MessageResponse create(SeriProductDTO seriDTO);
    
    boolean deletedByID(long id);
    
    SeriProductDTO update(long seriID , SeriProductDTO seriProductDTO);
    
    SeriDTO getById(long id);
    
    PaginationDTO getAllPagination(int no, int limit);
}
