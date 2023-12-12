package com.r2s.mobilestore.service;


import com.r2s.mobilestore.common.MessageResponse;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.data.dto.ReviewDTO;

public interface ReviewService {

    MessageResponse create(ReviewDTO reviewDTO);
    
    ReviewDTO update(long reviewID , ReviewDTO reviewDTO);
    
    boolean deleteById(long id);

    PaginationDTO getById(long id, int no, int limit);
    
    PaginationDTO getAllPagination(int no, int limit);
}
