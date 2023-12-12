package com.r2s.mobilestore.service;

import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.promotion.PromotionCreationDTO;
import com.r2s.mobilestore.data.dto.promotion.PromotionDTO;

public interface PromotionService {
    PromotionDTO create(PromotionCreationDTO promotionCreationDTO);

    PromotionDTO update(long id, PromotionCreationDTO promotionCreationDTO);

    PromotionDTO getById(long id);

    PaginationDTO getAll(int no , int limit);

    PaginationDTO findAllByDiscountCode(String discountCode,int no,int limit);

    boolean deleteById(long id);
}
