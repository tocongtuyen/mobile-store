package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.data.dto.ColorDTO;
import com.r2s.mobilestore.data.dto.ColorProductDTO;
import com.r2s.mobilestore.data.entity.Color;
import com.r2s.mobilestore.data.entity.Product;
import com.r2s.mobilestore.data.mapper.ColorMapper;
import com.r2s.mobilestore.data.mapper.ProductMapper;
import com.r2s.mobilestore.data.repository.ColorRepository;
import com.r2s.mobilestore.data.repository.ProductRepository;
import com.r2s.mobilestore.exception.InternalServerErrorException;
import com.r2s.mobilestore.exception.ResourceNotFoundException;
import com.r2s.mobilestore.exception.ValidationException;
import com.r2s.mobilestore.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author NguyenTienDat, VoTien
 */
@Service
public class ColorServiceImpl implements ColorService {
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private ColorMapper colorMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductRepository productRepository;

    /**
     * @param colorDTO
     * @return ColorDTO
     * @Author VoTien
     */
    @Override
    public ColorProductDTO create(ColorProductDTO colorDTO) {
        if (colorDTO.getName().isEmpty())
            throw new ValidationException(Collections.singletonMap("name color", colorDTO.getName()));

        if (!colorRepository.findColorByNameAndProId(colorDTO.getName(), colorDTO.getProduct_id()).isEmpty())
            throw new IllegalArgumentException("error.dataExist");
        Product product = productRepository.findById(colorDTO.getProduct_id()).orElseThrow(null);
        Color color = colorMapper.toColorEntity(colorDTO);
        color.setProduct(product);
        color.setStatus(true);
        return colorMapper.toColorDTO(colorRepository.save(color));
    }


    /**
     * @param product
     * @param colorDTOs
     * @return List<ColorDTO>
     * @author NguyenTienDat
     */
    @Override
    public List<ColorDTO> createProductColor(Product product, List<ColorDTO> colorDTOs) {

        List<ColorDTO> colorDTOsResult = new ArrayList<>();

        colorDTOs.forEach(colorDTO -> {
            Color color = colorMapper.toEntity(colorDTO);
            color.setProduct(product);
            color.setStatus(true);

            colorDTOsResult.add(colorMapper.toDTO(colorRepository.save(color)));
        });

        return colorDTOsResult;
    }

    /**
     * @param product
     * @param colorDTOs
     * @return List<ColorDTO>
     * @author NguyenTienDat
     */
    @Override
    public List<ColorDTO> updateProductColor(Product product, List<ColorDTO> colorDTOs) {

        List<ColorDTO> colorDTOsResult = new ArrayList<>();
        List<Color> colors = colorRepository.findByProductId(product.getId());
        List<ColorDTO> colorDTOsPrepare = new ArrayList<>();

        colors.forEach(color -> {
            colorDTOsPrepare.add(colorMapper.toDTO(color));
            if (!colorDTOs.contains(colorMapper.toDTO(color))) {
                // Update status = false (0) for color when consumer delete this color
                color.setStatus(false);
                colorRepository.save(color);
            } else {
                colorDTOs.forEach(colorDTO -> {
                    // if color existed in database, it will be updated then save it
                    if (colorDTO.getId() == color.getId()) {
                        Color colorMapped = colorMapper.toEntity(colorDTO);
                        colorMapped.setProduct(product);
                        colorMapped.setStatus(true);
                        colorDTOsResult.add(colorMapper.toDTO(colorRepository.save(colorMapped)));
                    }
                });
            }
        });
        // a foreach to check a colorDTO in list colorDTO have the new color, yes or no?
        // if existing new color then save this color into database
        colorDTOs.forEach(colorDTO -> {
            if (!colorDTOsPrepare.contains(colorDTO)) {

                Color colorMapped = colorMapper.toEntity(colorDTO);
                colorMapped.setProduct(product);

                colorMapped.setStatus(true);
                Color colorSaved = colorRepository.save(colorMapped);
                colorDTOsResult.add(colorMapper.toDTO(colorSaved));
            }
        });

        return colorDTOsResult;
    }


    /**
     * Method update color
     *
     * @param id
     * @param colorDTO
     * @return colorDTO if update success
     * @author huuduc
     */
    public ColorProductDTO update(int id, ColorProductDTO colorDTO) {

        Color oldColor = this.colorRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        if (!colorRepository.findColorByNameAndProductId(colorDTO.getName(), colorDTO.getProduct_id()).isEmpty()) {

            throw new InternalServerErrorException(
                    String.format("Exists color named %s", colorDTO.getName()));
        }

        Color updateColor = colorMapper.toColorEntity(colorDTO);
        updateColor.setId(oldColor.getId());
        updateColor.setStatus(true);

        colorRepository.save(updateColor);
        return colorMapper.toColorDTO(updateColor);
    }

    /**
     * Method delete color
     *
     * @param id
     * @return true if delete success
     * @author huuduc
     */
    @Override
    public boolean delete(int id) {

        Color oldColor = this.colorRepository.findById(id).orElseThrow(
                        () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        if (!oldColor.isStatus()) {
            throw new ResourceNotFoundException(Collections.singletonMap("id", id));
        }

        oldColor.setStatus(false);

        this.colorRepository.save(oldColor);

        return true;
    }
}
