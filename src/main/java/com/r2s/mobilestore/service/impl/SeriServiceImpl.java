package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.common.MessageResponse;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.data.dto.SeriDTO;
import com.r2s.mobilestore.data.entity.PaymentMethod;
import com.r2s.mobilestore.data.entity.Product;
import com.r2s.mobilestore.data.dto.SeriProductDTO;
import com.r2s.mobilestore.data.entity.Seri;
import com.r2s.mobilestore.data.entity.User;
import com.r2s.mobilestore.data.mapper.SeriMapper;
import com.r2s.mobilestore.data.repository.ProductRepository;
import com.r2s.mobilestore.data.repository.SeriRepository;
import com.r2s.mobilestore.data.repository.UserRepository;
import com.r2s.mobilestore.exception.InternalServerErrorException;
import com.r2s.mobilestore.exception.ResourceNotFoundException;
import com.r2s.mobilestore.service.ProductService;
import com.r2s.mobilestore.service.SeriService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author NguyenTienDat
 */
@Service
public class SeriServiceImpl implements SeriService {
    @Autowired
    private SeriMapper seriMapper;
    @Autowired
    private SeriRepository seriRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Method create seri
     *
     * @param seriDTO
     * @return MessageResponse
     * @Author Votien
     */
    @Override
    public MessageResponse create(SeriProductDTO seriDTO) {
        Seri seri = seriMapper.toSeriEntity(seriDTO);

        if (seriRepository.existsByName(seri.getName()))

            throw new InternalServerErrorException(
                    String.format("Exists seri named %s", seri.getName()));

        Product product = productRepository.findById(seriDTO.getProduct_id()).orElseThrow();

        seri.setProduct(product);
        seriRepository.save(seri);

        return new MessageResponse(HttpServletResponse.SC_OK, null, null);
    }

    /**
     * @param product
     * @param seriDTOs
     * @return List<SeriDTO>
     * @author NguyenTienDat
     */
    @Override
    public List<SeriDTO> createProductSeri(Product product, List<SeriDTO> seriDTOs) {

        List<SeriDTO> seriDTOsResult = new ArrayList<>();
        seriDTOs.forEach(seriDTO -> {

            Seri seri = seriMapper.toEntity(seriDTO);
            seri.setProduct(product);
            seriDTOsResult.add(seriMapper.toDTO(seriRepository.save(seri)));
        });
        return seriDTOsResult;
    }

    /**
     * @param product
     * @param seriDTOs
     * @return List<SeriDTO>
     * @author NguyenTienDat
     */
    @Override
    public List<SeriDTO> updateProductSeri(Product product, List<SeriDTO> seriDTOs) {

        List<SeriDTO> seriDTOsResult = new ArrayList<>();
        List<Seri> seris = seriRepository.findByProductId(product.getId());
        List<SeriDTO> seriDTOsPrepare = new ArrayList<>();

        seris.forEach(seri -> {
            seriDTOsPrepare.add(seriMapper.toDTO(seri));
            if (!seriDTOs.contains(seriMapper.toDTO(seri))) {
                // Update status = false (0) for seri when consumer delete this seri
                seri.setStatus(false);
                seriRepository.save(seri);
            } else {
                seriDTOs.forEach(seriDTO -> {
                    // if seri existed in database, it will be updated then save it
                    if (seriDTO.getId() == seri.getId()) {
                        Seri seriMapped = seriMapper.toEntity(seriDTO);
                        seriMapped.setProduct(product);
                        seriDTOsResult.add(seriMapper.toDTO(seriRepository.save(seriMapped)));
                    }
                });
            }
        });
        // a foreach to check a seriDTO in list seriDTO have the new seri, yes or no?
        // if existing new seri then save this seri into database
        seriDTOs.forEach(seriDTO -> {
            if (!seriDTOsPrepare.contains(seriDTO)) {
                Seri seriMapped = seriMapper.toEntity(seriDTO);
                seriMapped.setProduct(product);
                Seri seriSaved = seriRepository.save(seriMapped);
                seriDTOsResult.add(seriMapper.toDTO(seriSaved));
            }
        });

        return seriDTOsResult;
    }

    /**
     * Method update seri
     *
     * @param seriID
     * @param seriProductDTO
     * @return seriProductDTO if update is success
     * @author ngohoangkhactuong
     */
    @Override
    public SeriProductDTO update(long seriID, SeriProductDTO seriProductDTO) {

        //find seri by seriID .
        Seri oldSeri = seriRepository.findById(seriID).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", seriID))
        );

        Seri updatedSeri = seriMapper.toSeriEntity(seriProductDTO);
        updatedSeri.setId(oldSeri.getId());
        updatedSeri.setStatus(true);

        seriRepository.save(updatedSeri);

        return seriMapper.toSeriProductDTO(updatedSeri);
    }

    /**
     * Method delete seri
     *
     * @param id
     * @return true if delete is success
     * @author ngohoangkhactuong
     */
    public boolean deletedByID(long id) {

        Seri seri = seriRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", id))
        );

        // check if seri status is true -> delete
        if (!seri.isStatus()) {
            throw new ResourceNotFoundException(Collections.singletonMap("id", id));
        }

        seri.setStatus(false);
        this.seriRepository.save(seri);

        return true;
    }

    @Override
    public SeriDTO getById(long id) {
        Seri seri = seriRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        return seriMapper.toDTO(seri);
    }

    @Override
    public PaginationDTO getAllPagination(int no, int limit) {
        Page<SeriDTO> page = this.seriRepository.findAll(
                PageRequest.of(no, limit)).map(item -> seriMapper.toDTO(item));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(),
                page.getTotalElements(), page.getSize(),
                page.getNumber());
    }
}
