package com.r2s.mobilestore.service;

import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.ProductTechDTO;
import com.r2s.mobilestore.data.entity.Product;

import java.util.List;

/**
 * @author NguyenTienDat
 */
public interface ProductTechService {

    ProductTechDTO create(ProductTechDTO productTechDTO);

    List<ProductTechDTO> createProductTech(Product product, List<ProductTechDTO> productTechDTOs);

    List<ProductTechDTO> updateProductTech(Product product, List<ProductTechDTO> productTechDTOs);

    ProductTechDTO update(int id,String info);

    boolean deleteById(int id);
    
    ProductTechDTO getById(int id);
    
    PaginationDTO getAllPagination(int no, int limit);
}
