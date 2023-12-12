package com.r2s.mobilestore.data.dto.oder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.r2s.mobilestore.constant.Constant;
import com.r2s.mobilestore.data.dto.*;
import com.r2s.mobilestore.data.dto.product.ProductOrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreationDTO {

    private long idUser;
    private long idPromotion;
    private String paymentMethodDTO;
    private StatusDTO statusDTO;
    List<ProductOrderDTO> orderProductDTOList;
    private long idAddress;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DATE_FORMAT)
    private Date receiveDate;
}
