package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.data.dto.MemoryDTO;
import com.r2s.mobilestore.data.entity.Memory;
import com.r2s.mobilestore.data.entity.Product;
import com.r2s.mobilestore.data.mapper.MemoryMapper;
import com.r2s.mobilestore.data.repository.MemoryRepository;
import com.r2s.mobilestore.service.MemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NguyenTienDat
 */
@Service
public class MemoryServiceImpl implements MemoryService {

    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private MemoryMapper memoryMapper;

    /**
     *
     * @param memoryDTO
     * @return MemoryDTO
     * @Author VoTien
     */
    @Override
    public MemoryDTO create(MemoryDTO memoryDTO) {
        return memoryMapper.toDTO(memoryRepository.save(memoryMapper.toEntity(memoryDTO)));
    }

    /**
     *
     * @param product
     * @param memoryDTOs
     * @return List<MemoryDTO>
     * @author NguyenTienDat
     */
    @Override
    public List<MemoryDTO> createProductMemory(Product product, List<MemoryDTO> memoryDTOs){

        List<MemoryDTO> memoryDTOsRessult = new ArrayList<>();

        memoryDTOs.forEach(memoryDTO -> {

            Memory memory = memoryMapper.toEntity(memoryDTO);
            memory.setProduct(product);

            memoryDTOsRessult.add(memoryMapper.toDTO(memoryRepository.save(memory)));
        });
        return memoryDTOsRessult;
    }

    /**
     *
     * @param product
     * @param memoryDTOs
     * @return List<MemoryDTO>
     * @author NguyenTienDat
     */
    @Override
    public List<MemoryDTO> updateProductMemory(Product product, List<MemoryDTO> memoryDTOs){

        List<MemoryDTO> memoryDTOsResult = new ArrayList<>();
        List<Memory> memorys = memoryRepository.findByProductId(product.getId());
        List<MemoryDTO> memoryDTOsPrepare = new ArrayList<>();

        memorys.forEach(memory -> {
            memoryDTOsPrepare.add(memoryMapper.toDTO(memory));
            if (!memoryDTOs.contains(memoryMapper.toDTO(memory))){
                // Update status = false (0) for memory when consumer delete this memory
                memory.setStatus(false);
                memoryRepository.save(memory);
            } else {
                memoryDTOs.forEach(memoryDTO -> {
                    // if memory existed in database, it will be updated then save it
                    if (memoryDTO.getId() == memory.getId()) {
                        Memory memoryMapped=  memoryMapper.toEntity(memoryDTO);
                        memoryMapped.setProduct(product);
                        memoryDTOsResult.add(memoryMapper.toDTO(memoryRepository.save(memoryMapped)));
                    }
                });
            }
        });
        // a foreach to check a memoryDTO in list memoryDTO have the new memory, yes or no?
        // if existing new memory then save this memory into database
        memoryDTOs.forEach(memoryDTO -> {
            if (!memoryDTOsPrepare.contains(memoryDTO)) {
                Memory memoryMapped=  memoryMapper.toEntity(memoryDTO);
                memoryMapped.setProduct(product);
                Memory memorySaved = memoryRepository.save(memoryMapped);
                memoryDTOsResult.add(memoryMapper.toDTO(memorySaved));
            }
        });

        return memoryDTOsResult;
    }
}
