package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.data.dto.TechnicalDTO;
import com.r2s.mobilestore.data.entity.Color;
import com.r2s.mobilestore.data.entity.PaymentMethod;
import com.r2s.mobilestore.data.entity.Technical;
import com.r2s.mobilestore.data.entity.User;
import com.r2s.mobilestore.data.mapper.TechnicalMapper;
import com.r2s.mobilestore.data.repository.TechnicalRepository;
import com.r2s.mobilestore.data.repository.UserRepository;
import com.r2s.mobilestore.exception.ConflictException;
import com.r2s.mobilestore.exception.InternalServerErrorException;
import com.r2s.mobilestore.exception.ResourceNotFoundException;
import com.r2s.mobilestore.exception.ValidationException;
import com.r2s.mobilestore.service.TechnicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author NguyenTienDat
 */

@Service
public class TechnicalServiceImpl implements TechnicalService {

    @Autowired
    private TechnicalRepository technicalRepository;
    @Autowired
    private TechnicalMapper technicalMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageSource messageSource;

    /**
     * Method create technical
     *
     * @param technicalDTO
     * @return TechnicalDTO
     * @author NguyenTienDat
     */
    @Override
    public TechnicalDTO create(TechnicalDTO technicalDTO) {
        return technicalMapper.toDTO(technicalRepository.save(technicalMapper.toEntity(technicalDTO)));
    }

    /**
     * Method create technical having name
     * @param technicalDTO
     * @return TechnicalDTO
     * @author NguyenTienDat
     */
    @Override
    public TechnicalDTO createTechnical(TechnicalDTO technicalDTO) {

        Technical technical = technicalMapper.toEntity(technicalDTO);

        return technicalMapper.toDTO(technicalRepository.save(technical));
    }
    /**
     * Method create technical having name
     * @author NguyenTienDat
     * @param name
     * @return show TechnicalDTO if success
     */
    @Override
    public TechnicalDTO create(String name) {

        if(name.isEmpty())
            throw new ValidationException(Collections.singletonMap("name technical", name));

        if(technicalRepository.existsByName(name))
            throw new IllegalArgumentException("error.dataExist");

        Technical technical = new Technical();
        technical.setName(name);

        return technicalMapper.toDTO(technicalRepository.save(technical));
    }

    /**
     * Method update technical
     *
     * @param technicalDTO
     * @return show TechnicalDTO if success
     * @author huuduc
     */
    @Override
    public TechnicalDTO update(int id, TechnicalDTO technicalDTO) {

        Map<String, Object> errors = new HashMap<>();

        Technical foundTechnical = this.technicalRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        //Check if exits name
        if(technicalRepository.existsByName(technicalDTO.getName())) {
            errors.put("name", technicalDTO.getName());
        }

        //throw conflict exception
        if (errors.size() > 0) {
            throw new ConflictException(Collections.singletonMap("TechnicalDTO", errors));
        }

        Technical updateTechnical = technicalMapper.toEntity(technicalDTO);
        updateTechnical.setId(foundTechnical.getId());

        return this.technicalMapper.toDTO(this.technicalRepository.save(updateTechnical));
    }
    
    @Override
    public TechnicalDTO getById(int id){
        Technical technical = technicalRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        return technicalMapper.toDTO(technical);
    }
    
    @Override
    public PaginationDTO getAllPagination(int no, int limit) {
        Page<TechnicalDTO> page = this.technicalRepository.findAll(
        		PageRequest.of(no, limit)).map(item -> technicalMapper.toDTO(item));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(),
                page.getTotalElements(), page.getSize(),
                page.getNumber());
    }
}
