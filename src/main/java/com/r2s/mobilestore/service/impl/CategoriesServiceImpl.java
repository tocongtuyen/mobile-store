package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.common.MessageResponse;
import com.r2s.mobilestore.data.dto.CategoriesDTO;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.data.entity.Categories;
import com.r2s.mobilestore.data.entity.PaymentMethod;
import com.r2s.mobilestore.data.entity.User;
import com.r2s.mobilestore.data.mapper.CategoriesMapper;
import com.r2s.mobilestore.data.repository.CategoriesRepository;
import com.r2s.mobilestore.data.repository.UserRepository;
import com.r2s.mobilestore.exception.ConflictException;
import com.r2s.mobilestore.exception.InternalServerErrorException;
import com.r2s.mobilestore.exception.ResourceNotFoundException;
import com.r2s.mobilestore.service.CategoriesService;
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
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private CategoriesMapper categoriesMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageSource messageSource;

    public List<CategoriesDTO> getAll() {
        return categoriesRepository.findByStatusIsTrue().stream().map(u -> categoriesMapper.toDTO(u)).collect(Collectors.toList());
    }
    @Override
    public CategoriesDTO getById(long id){
        Categories categories = categoriesRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        return categoriesMapper.toDTO(categories);
    }

    @Override
    public PaginationDTO getAllPagination(int no, int limit) {
        Page<CategoriesDTO> page = this.categoriesRepository.findAll(
        		PageRequest.of(no, limit)).map(item -> categoriesMapper.toDTO(item));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(),
                page.getTotalElements(), page.getSize(),
                page.getNumber());
    }

    /**
     * Method create categories of products
     *
     * @param categoriesDTO
     * @return Returns an "ok" response if the address update is successful
     */
    @Override
    public MessageResponse create(CategoriesDTO categoriesDTO) {

        Categories categories = categoriesMapper.toEntity(categoriesDTO);

        if (categoriesRepository.existsByName(categories.getName()))
            throw new InternalServerErrorException(String.format("Exists categories named %s", categories.getName()));
        categoriesRepository.save(categories);

        return new MessageResponse(HttpServletResponse.SC_OK, "created Category", null);
    }

    /**
     * Method update category
     *
     * @param id
     * @param categoriesDTO
     * @return CategoriesDTO if update success
     * @author huuduc
     */
    @Override
    public CategoriesDTO update(long id, CategoriesDTO categoriesDTO) {

        Categories oldCategories = this.categoriesRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        Map<String, Object> errors = new HashMap<>();

        //Check if exits name
        if (categoriesRepository.existsByName(categoriesDTO.getName())) {
            errors.put("name", categoriesDTO.getName());
        }

        //throw conflict exception
        if (errors.size() > 0) {
            throw new ConflictException(Collections.singletonMap("categoriesDTO", errors));
        }

        Categories updateCategories = this.categoriesMapper.toEntity(categoriesDTO);
        updateCategories.setId(oldCategories.getId());

        return this.categoriesMapper.toDTO(categoriesRepository.save(updateCategories));
    }

    /**
     * Method delete (set status) category by id
     *
     * @param id
     * @return true if delete success
     * @author huuduc
     */
    @Override
    public boolean deleteById(long id) {

        Categories oldCategories = this.categoriesRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        //Check category deleted
        if (!oldCategories.isStatus()) {
            throw new ResourceNotFoundException(Collections.singletonMap("id", id));
        }

        oldCategories.setStatus(false);
        this.categoriesRepository.save(oldCategories);

        return true;
    }
}
