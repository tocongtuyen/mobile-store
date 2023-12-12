package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.data.dto.SeriDTO;
import com.r2s.mobilestore.data.entity.PaymentMethod;
import com.r2s.mobilestore.data.entity.Seri;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    @Mapping(target = "status",constant = "true")
    PaymentMethod toEnity(PaymentMethodDTO paymentMethodDTO);

    PaymentMethodDTO toDTO(PaymentMethod paymentMethod);
}
