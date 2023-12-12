package com.r2s.mobilestore.service;

import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.oder.OrderCreationDTO;
import com.r2s.mobilestore.data.dto.oder.OrderDetailsDTO;
import com.r2s.mobilestore.data.dto.oder.OrderUpdateDTO;
import com.r2s.mobilestore.data.dto.oder.ShowOrderDetailsDTO;
import com.r2s.mobilestore.data.entity.Color;
import com.r2s.mobilestore.data.entity.Memory;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    OrderDetailsDTO create(OrderCreationDTO orderCreationDTO);

    BigDecimal totalProduct(BigDecimal total, int discount, BigDecimal totalPurchase, BigDecimal maxGet);

    boolean deleteOrderByIdUser(long id);

    @Transactional
    OrderDetailsDTO update(long order_id, OrderUpdateDTO orderUpdateDTO);

    PaginationDTO showOrderByUser(int no, int limit);

    ShowOrderDetailsDTO getOrderDetailsDTO(long orderId);

    PaginationDTO showAllOrderByAdmin(int no, int limit);

    Boolean deleteOrder(long id);

    Boolean deleteOrderByCustomer(long orderId);

    Boolean cancelOrder(long idOrder);
}
