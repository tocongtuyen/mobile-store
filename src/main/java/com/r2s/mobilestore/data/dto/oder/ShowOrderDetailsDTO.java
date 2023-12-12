package com.r2s.mobilestore.data.dto.oder;

import com.r2s.mobilestore.data.dto.address.AddressDTO;
import com.r2s.mobilestore.data.dto.product.ShowProductOrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowOrderDetailsDTO{

    private long id;
    private OrderDTO orderDTO;
    private int quantity;
    private AddressDTO address;
    private List<ShowProductOrderDTO> orderProductDTOList;

}
