package com.r2s.mobilestore.data.mapper;

import com.r2s.mobilestore.data.dto.oder.OrderCreationDTO;
import com.r2s.mobilestore.data.dto.oder.OrderDTO;
import com.r2s.mobilestore.data.dto.oder.OrderDetailsDTO;
import com.r2s.mobilestore.data.dto.oder.OrderUpdateDTO;
import com.r2s.mobilestore.data.dto.oder.ShowOrderDTO;
import com.r2s.mobilestore.data.entity.OrderDetails;
import com.r2s.mobilestore.data.entity.Orders;
import org.hibernate.criterion.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {  PaymentMethodMapper.class,StatusMapper.class, UserMapper.class})
@Component
public interface OrderMapper {
    @Mapping(source = "total", target = "total")
    @Mapping(source = "receiveDate", target = "receiveDate")
    @Mapping(source = "user", target = "userDTO")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    @Mapping(source = "paymentMethod", target = "paymentMethodDTO")
    @Mapping(source = "status", target = "statusDTO")
    OrderDTO toDTO(Orders orders);

    @Mapping(source = "statusDTO", target = "status")
    Orders toEnity(OrderCreationDTO orderCreationDTO);

    @Mapping(source = "orderDTO", target = "orders")
    @Mapping(ignore = true, target = "quantity")
    @Mapping(ignore = true, target = "memory")
    @Mapping(ignore = true, target = "seri")
    @Mapping(ignore = true, target = "color")
    OrderDetails toDetailEnity(OrderDetailsDTO orderDetailsDTO);


    Orders toOrderUpdateEntity(OrderUpdateDTO orderUpdateDTO);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "receiveDate", target = "receiveDate")
    @Mapping(source = "status", target = "statusDTO")
    ShowOrderDTO toShowOrderDTO( Orders orders);

    @Mapping(source = "orders", target = "orderDTO")
    OrderDetailsDTO toDetailDTO(OrderDetails orderDetails);
}
