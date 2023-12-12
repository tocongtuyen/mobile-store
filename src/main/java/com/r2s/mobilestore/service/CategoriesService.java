package com.r2s.mobilestore.service;

import com.r2s.mobilestore.common.MessageResponse;
import com.r2s.mobilestore.data.dto.CategoriesDTO;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.PaymentMethodDTO;

import java.util.List;

public interface CategoriesService {


    MessageResponse create(CategoriesDTO categoriesDTO);

    CategoriesDTO update(long id , CategoriesDTO categoriesDTO);

    boolean deleteById(long id);

    CategoriesDTO getById(long id);
    
    PaginationDTO getAllPagination(int no, int limit);
    
}
