package com.r2s.mobilestore.service;

import com.r2s.mobilestore.data.dto.MemoryDTO;
import com.r2s.mobilestore.data.entity.Product;

import java.util.List;
/**
 * @author NguyenTienDat
 */
public interface MemoryService {

    MemoryDTO create(MemoryDTO memoryDTO);

    List<MemoryDTO> createProductMemory(Product product, List<MemoryDTO> memoryDTOs);

    List<MemoryDTO> updateProductMemory(Product product, List<MemoryDTO> memoryDTOs);

}
