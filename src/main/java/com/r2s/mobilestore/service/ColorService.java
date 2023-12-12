package com.r2s.mobilestore.service;

import com.r2s.mobilestore.data.dto.ColorDTO;

import com.r2s.mobilestore.data.dto.ColorProductDTO;
import com.r2s.mobilestore.data.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * @author NguyenTienDat
 */
public interface ColorService {

    ColorProductDTO create(ColorProductDTO colorDTO);

    List<ColorDTO> createProductColor(Product product, List<ColorDTO> colorDTOs);

    List<ColorDTO> updateProductColor(Product product, List<ColorDTO> colorDTOs);


    ColorProductDTO update(int id , ColorProductDTO colorDTO);

    boolean delete(int id);



}
