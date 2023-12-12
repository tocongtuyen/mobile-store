package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.ImageDTO;
import com.r2s.mobilestore.data.entity.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    Image toEntity(ImageDTO imageDTO);

    ImageDTO toDTO(Image image);
}
