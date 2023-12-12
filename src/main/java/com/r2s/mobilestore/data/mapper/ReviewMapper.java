package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.ReviewDTO;
import com.r2s.mobilestore.data.dto.ShowReviewDTO;
import com.r2s.mobilestore.data.entity.Review;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ProductMapper.class})
public interface ReviewMapper {

    @Mapping(source = "user_name", target = "user.fullName")
    @Mapping(source = "product_id", target = "product.id")
    @Mapping(source = "comment", target = "comment")
    @Mapping(source = "rating", target = "rating")
    Review toEntity(ReviewDTO reviewDTO);

    @Mapping(source = "user.fullName", target = "user_name")
    @Mapping(source = "product.id", target = "product_id")
    @Mapping(source = "comment", target = "comment")
    @Mapping(source = "rating", target = "rating")
    ReviewDTO toDTO(Review review);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.fullName", target = "user_name")
    @Mapping(source = "product.id", target = "product_id")
    @Mapping(source = "comment", target = "comment")
    @Mapping(source = "rating", target = "rating")
    ShowReviewDTO toShowDTO(Review review);
}
