package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.common.MessageResponse;
import com.r2s.mobilestore.common.enumeration.ENum;
import com.r2s.mobilestore.data.dto.ManufacturerDTO;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.data.entity.Categories;
import com.r2s.mobilestore.data.entity.Manufacturer;
import com.r2s.mobilestore.data.entity.PaymentMethod;
import com.r2s.mobilestore.data.entity.User;
import com.r2s.mobilestore.data.mapper.ManufacturerMapper;
import com.r2s.mobilestore.data.repository.ManufacturerRepository;
import com.r2s.mobilestore.data.repository.UserRepository;
import com.r2s.mobilestore.exception.ConflictException;
import com.r2s.mobilestore.exception.InternalServerErrorException;
import com.r2s.mobilestore.exception.ResourceNotFoundException;
import com.r2s.mobilestore.service.ManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author NguyenTienDat
 */
@Service
public class ManufacturerServiceImpl implements ManufacturerService {

    @Autowired
    private ManufacturerRepository manufacturerRepository;
    @Autowired
    private ManufacturerMapper manufacturerMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageSource messageSource;

    public List<ManufacturerDTO> getAll() {
        return manufacturerRepository.findByStatusIsTrue().stream().map(u -> manufacturerMapper.toDTO(u)).collect(Collectors.toList());
    }

    public ManufacturerDTO getById(long id) {
        Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        return manufacturerMapper.toDTO(manufacturer);
    }

    @Override
    public PaginationDTO getAllPagination(int no, int limit) {
        Page<ManufacturerDTO> page = this.manufacturerRepository.findAll(
                PageRequest.of(no, limit)).map(item -> manufacturerMapper.toDTO(item));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(),
                page.getTotalElements(), page.getSize(),
                page.getNumber());
    }

    /**
     * Method create manufacturer for products
     *
     * @param manufacturerDTO
     * @return Returns an "ok" response if the address update is successful
     * @Author
     */
    @Override
    public MessageResponse create(ManufacturerDTO manufacturerDTO) {

        Manufacturer manufacturer = manufacturerMapper.toEntity(manufacturerDTO);

        if (manufacturerRepository.existsByName(manufacturer.getName()))
            throw new InternalServerErrorException(String.format("Exists manufacturer named %s", manufacturer.getName()));
        manufacturerRepository.save(manufacturer);

        return new MessageResponse(HttpServletResponse.SC_OK, "Created Manufacturer", null);
    }


    /**
     * Method update manufacture
     *
     * @param id
     * @param manufacturerDTO
     * @return manufacturerDTO if update success
     * @author huuduc
     */
    @Override
    public ManufacturerDTO update(long id, ManufacturerDTO manufacturerDTO) {

        Manufacturer oldManufacturer = manufacturerRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        Map<String, Object> errors = new HashMap<>();

        //Check if exits name
        if (manufacturerRepository.existsByName(manufacturerDTO.getName())) {
            errors.put("name", manufacturerDTO.getName());
        }

        //throw conflict exception
        if (errors.size() > ENum.ZERO.getValue()) {
            throw new ConflictException(Collections.singletonMap("manufacturerDTO", errors));
        }

        Manufacturer updateManufacturer = this.manufacturerMapper.toEntity(manufacturerDTO);
        updateManufacturer.setId(oldManufacturer.getId());

        return this.manufacturerMapper.toDTO(manufacturerRepository.save(updateManufacturer));
    }

    /**
     * Method delete manufacture
     *
     * @param id
     * @return true if delete success
     * @author huuduc
     */
    @Override
    public boolean deleteById(long id) {

        Manufacturer oldManufacturer = this.manufacturerRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        //Check manufacture deleted
        if (!oldManufacturer.isStatus()) {
            throw new ResourceNotFoundException(Collections.singletonMap("id", id));
        }

        oldManufacturer.setStatus(false);
        this.manufacturerRepository.save(oldManufacturer);

        return true;
    }
}
