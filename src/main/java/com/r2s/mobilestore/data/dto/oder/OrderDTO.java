package com.r2s.mobilestore.data.dto.oder;

import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.data.dto.StatusDTO;
import com.r2s.mobilestore.data.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private long id;
    private BigDecimal total;
    private Date receiveDate;
    private boolean paymentStatus;
    private PaymentMethodDTO paymentMethodDTO;
    private UserDTO userDTO;
    private StatusDTO statusDTO;
}
