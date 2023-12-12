package com.r2s.mobilestore.service;

import com.r2s.mobilestore.data.dto.ImageDTO;
import com.r2s.mobilestore.data.entity.Product;

import java.util.List;
/**
 * @author NguyenTienDat
 */
public interface ImageService {
    ImageDTO create(ImageDTO imageDTO);

    List<ImageDTO> createProductImage(Product product, List<ImageDTO> imageDTOs);

    List<ImageDTO> updateProductImage(Product product, List<ImageDTO> imageDTOs);

}
