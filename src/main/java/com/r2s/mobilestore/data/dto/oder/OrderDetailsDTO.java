package com.r2s.mobilestore.data.dto.oder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.r2s.mobilestore.data.dto.product.ProductOrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDTO {
    @JsonIgnore
    private long id;
    private OrderDTO orderDTO;
    private int quantity;
    List<ProductOrderDTO> orderProductDTOList;
 }
