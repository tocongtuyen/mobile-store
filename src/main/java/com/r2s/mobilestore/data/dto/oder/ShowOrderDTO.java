package com.r2s.mobilestore.data.dto.oder;

import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.data.dto.StatusDTO;
import com.r2s.mobilestore.data.dto.address.AddressDTO;
import com.r2s.mobilestore.data.dto.product.ProductOrderDTO;
import com.r2s.mobilestore.data.dto.user.UserDTO;
import com.r2s.mobilestore.data.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowOrderDTO {
    private long id;
    private BigDecimal total;
    private Date receiveDate;
    private StatusDTO statusDTO;
    private ProductOrderDTO productOrderDTO;
    private  long quantity;
}
