package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.common.DateTime;
import com.r2s.mobilestore.common.enumeration.ENum;
import com.r2s.mobilestore.common.enumeration.EPaymentMethod;
import com.r2s.mobilestore.common.enumeration.Estatus;
import com.r2s.mobilestore.data.dto.oder.OrderCreationDTO;
import com.r2s.mobilestore.data.dto.oder.OrderDTO;
import com.r2s.mobilestore.data.dto.oder.OrderDetailsDTO;
import com.r2s.mobilestore.data.dto.oder.OrderUpdateDTO;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.oder.*;
import com.r2s.mobilestore.data.dto.product.ProductDetailsDTO;
import com.r2s.mobilestore.data.dto.product.ProductOrderDTO;
import com.r2s.mobilestore.data.dto.product.ShowProductOrderDTO;
import com.r2s.mobilestore.data.entity.*;
import com.r2s.mobilestore.data.mapper.*;
import com.r2s.mobilestore.data.repository.*;
import com.r2s.mobilestore.exception.*;
import com.r2s.mobilestore.service.OrderService;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.InterruptedIOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private OrdersRepository orderRepository;
    @Autowired
    private SeriRepository seriRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private ColorMapper colorMapper;
    @Autowired
    private MemoryMapper memoryMapper;
    @Autowired
    private StatusMapper statusMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private EntityManager entityManager;

    /**
     * Method create order by user
     *
     * @param orderCreationDTO
     * @return OrderDetailsDTO
     * @Author VoTien
     */
    public OrderDetailsDTO create(OrderCreationDTO orderCreationDTO) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
                        null, null)));



        BigDecimal total = new BigDecimal(ENum.ZERO.getValue());

        for (ProductOrderDTO productOrderDTO : orderCreationDTO.getOrderProductDTOList()) {
            total = total.add(productOrderDTO.getPrice());
        }
        Promotion promotion = null;
        if(orderCreationDTO.getIdPromotion() != 0){
            promotion = promotionRepository.findById(orderCreationDTO.getIdPromotion()).orElseThrow(
                    () -> new ResourceNotFoundException(Collections.singletonMap("Promotion ID",
                            orderCreationDTO.getIdPromotion()))
            );

            total = totalProduct(total, promotion.getDiscount(), promotion.getTotalPurchase(), promotion.getMaxGet());

        }

        Orders orders = orderMapper.toEnity(orderCreationDTO);
        orders.setTotal(total);
        if(orderCreationDTO.getIdPromotion() == 0)
            orders.setPromotion(promotion);
        orders.setUser(user);
        orders.setPaymentMethod(paymentMethodRepository.findByName(orderCreationDTO.getPaymentMethodDTO()));

        orders.setReceiveDate(java.sql.Timestamp.valueOf(LocalDateTime.now().plusDays(ENum.ONE.getValue())));

        //Set receive date if customer payment is not cash (else set null)
        if (!orderCreationDTO.getPaymentMethodDTO().equals(EPaymentMethod.Cash.toString())) {
            orders.setPaymentStatus(true);
        } else {
            orders.setPaymentStatus(false);
        }

        OrderDTO orderDTO = orderMapper.toDTO(orderRepository.save(orders));

        OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
        orderDetailsDTO.setOrderDTO(orderDTO);
        orderDetailsDTO.setQuantity(orderCreationDTO.getOrderProductDTOList().size());
        orderDetailsDTO.setOrderProductDTOList(orderCreationDTO.getOrderProductDTOList());

        for (int i = 0; i < orderCreationDTO.getOrderProductDTOList().size(); i++) {

            String color = orderCreationDTO.getOrderProductDTOList().get(i).getColor();
            String memory = orderCreationDTO.getOrderProductDTOList().get(i).getMemory();
            String seri = orderCreationDTO.getOrderProductDTOList().get(i).getSeri();

            OrderDetails orderDetails = orderMapper.toDetailEnity(orderDetailsDTO);
            orderDetails.setQuantity(ENum.ONE.getValue());

            orderDetails.setAddress(addressRepository.findAddressById(orderCreationDTO.getIdAddress())
                    .orElse(addressRepository.findDefaultAddress(orderCreationDTO.getIdUser())));

            orderDetails.setColor(
                    colorRepository.findColorByNameAndProductId(
                            color,
                            orderCreationDTO.getOrderProductDTOList().get(i).getId()).orElseThrow(
                            () -> new ValidationException(Collections.singletonMap("color name",
                                    color)))
            );

            orderDetails.setMemory(
                    memoryRepository.findMemoryByNameAndProductId(
                            memory,
                            orderCreationDTO.getOrderProductDTOList().get(i).getId()).orElseThrow(
                            () -> new ValidationException(Collections.singletonMap("memory name",
                                    memory)))
            );

            orderDetails.setSeri(
                    seriRepository.findSeriByNameAndProductId(
                            seri,
                            orderCreationDTO.getOrderProductDTOList().get(i).getId()).orElseThrow(
                            () -> new ValidationException(Collections.singletonMap("seri name",
                                    seri)))
            );

            orderDetailRepository.save(orderDetails);
        }

        return orderDetailsDTO;
    }

    /**
     * Method Calculating the bill of order
     *
     * @param total
     * @param discount
     * @param totalPurchase
     * @param maxGet
     * @return BigDecimal
     * @Author Votien
     */
    public BigDecimal totalProduct(BigDecimal total, int discount, BigDecimal totalPurchase, BigDecimal maxGet) {

        if (total.compareTo(totalPurchase) > ENum.ZERO.getValue()) {

            BigDecimal discountBigDecimal = new BigDecimal(discount).divide(new BigDecimal(100));

            BigDecimal discountValue = total.multiply(discountBigDecimal);

            if (discountValue.compareTo(maxGet) > ENum.ZERO.getValue())
                discountValue = maxGet;

            total = total.subtract(discountValue);
        }

        return total;
    }


    /**
     * Method Check valid id color
     *
     * @param list
     * @param id
     * @return boolean
     * @Author VoTien
     */
    public boolean checkValidIdColor(List<Color> list, long id) {

        for (Color color : list) {

            if (color.getId() == id)
                return true;
        }

        return false;
    }


    /**
     * Method check valid id memory
     *
     * @param list
     * @param id
     * @return boolean
     * @Author VoTien
     */
    public boolean checkValidIdMemory(List<Memory> list, long id) {

        for (Memory memory : list) {

            if (memory.getId() == id)
                return true;
        }

        return false;
    }


    /**
     * Method set order status to "Cancel" by user using order id
     *
     * @param id of order
     * @return return true if order is canceled successfully
     * @Author AnhTuan
     */
    public boolean deleteOrderByIdUser(long id) {

        //Get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
                        null, null)));

        //Get current user's order
        Orders order = orderRepository.findOrderByUserId(user.getId(), id);

        //Order don't exist
        if (order == null) {
            throw new ResourceNotFoundException(Collections.singletonMap("id", id));
        }

        //Order has been paid or transfer (Need to have payment_method name = "Cash" in database and paymentStatus is not NULL)
        if (order.isPaymentStatus() || !order.getPaymentMethod().getName().equals("Cash")) {
            new InternalServerErrorException(this.messageSource.getMessage("error.orderHasBeenPaid", null, null));
        }

        //Order is not ready (Need to have status name = "Default" in database)
        if (!order.getStatus().getName().equals("Active")) {
            throw new InternalServerErrorException(String.format("Your order is %s", order.getStatus().getName()));
        }

        //Set status "Cancel" for order
        order.setStatus(statusRepository.findByName("Cancel").get());

        this.orderRepository.save(order);

        return true;
    }


    /**
     * Method show detail order by order id
     *
     * @param orderId
     * @return ShowOrderDetailsDTO
     * @author TuongVi
     */
    public ShowOrderDetailsDTO getOrderDetailsDTO(long orderId) {
        int defaultNum = 0;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
                        null, null)));

        int roleAdmin = 1;
        List<OrderDetails> orderDetailsList = new ArrayList<>();

        if (user.getRole().getId() == roleAdmin) {

            orderDetailsList = this.orderDetailRepository.findAllByOrderId(orderId);

            user = orderDetailsList.get(defaultNum).getOrders().getUser();

        } else {

            orderDetailsList = this.orderDetailRepository
                    .findAllByOrderIdAndUserId(orderId, user.getId());
        }

        ShowOrderDetailsDTO orderDetailsDTO = new ShowOrderDetailsDTO();

        if (orderDetailsList.isEmpty()) {

            throw new ResourceNotFoundException(Collections.singletonMap("id", orderId));
        }

        List<ShowProductOrderDTO> productOrderDTOList = orderDetailsList.stream()
                .map(this::mapToProductOrderDTO)
                .collect(Collectors.toList());

        orderDetailsDTO.setId(orderId);
        orderDetailsDTO.setOrderDTO(orderMapper.toDTO(orderDetailsList.get(defaultNum).getOrders()));
        orderDetailsDTO.setQuantity(orderDetailsList.size());
        orderDetailsDTO.setOrderProductDTOList(productOrderDTOList);

        user.getAddressList().stream()
                .filter(Address::isDefaults)
                .findFirst()
                .ifPresent(address -> orderDetailsDTO.setAddress(addressMapper.toDTO(address)));

        return orderDetailsDTO;
    }

    /**
     * Method show Order of User
     *
     * @param no
     * @param limit
     * @return PaginationDTO(ShowOrderDTO)
     * @author TuongVi
     */
    public PaginationDTO showOrderByUser(int no, int limit) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
                        null, null)));


        Page<Orders> page = this.orderRepository.findAllByUserId(user.getId(), PageRequest.of(no, limit));

        List<ShowOrderDTO> showOrderDTOList = page.getContent().stream().map(this::mapToShowOrderDTO)
                .collect(Collectors.toList());


        for (ShowOrderDTO order : showOrderDTOList) {
            order.setQuantity(orderDetailRepository.CountByOrderId(order.getId()) - 1);
        }

        return new PaginationDTO(showOrderDTOList, page.isFirst(), page.isLast(), page.getTotalPages(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }

    /**
     * Method show all Order of Admin
     *
     * @param no
     * @param limit
     * @return PaginationDTO(ShowOrderDTO)
     * @author TuongVi
     */
    public PaginationDTO showAllOrderByAdmin(int no, int limit) {

        Page<Orders> page = this.orderRepository.findAll(PageRequest.of(no, limit));

        List<ShowOrderDTO> showOrderDTOList = page.getContent().stream().map(this::mapToShowOrderDTO)
                .collect(Collectors.toList());

        return new PaginationDTO(showOrderDTOList, page.isFirst(), page.isLast(), page.getTotalPages(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }

    /**
     * Method map order to ShowOrderDTO
     *
     * @param order
     * @return PaginationDTO(OrderDTO)
     * @author TuongVi
     */
    public ShowOrderDTO mapToShowOrderDTO(Orders order) {
        int defaultNum = 0;
        ShowOrderDTO showOrderDTO = new ShowOrderDTO();
        ShowOrderDetailsDTO showOrderDetailsDTO = getOrderDetailsDTO(order.getId());

        showOrderDTO = this.orderMapper.toShowOrderDTO(order);
        showOrderDTO.setProductOrderDTO(showOrderDetailsDTO.getOrderProductDTOList().get(defaultNum));

        return showOrderDTO;
    }

    /**
     * Method map orderDetail to ShowProductOrderDTO
     *
     * @param orderDetail
     * @return ShowProductOrderDTO
     * @author TuongVi
     */
    private ShowProductOrderDTO mapToProductOrderDTO(OrderDetails orderDetail) {
        int defaultNum = 0;
        Product product = orderDetail.getSeri().getProduct();
        ShowProductOrderDTO productOrderDTO = new ShowProductOrderDTO();

        productOrderDTO.setSeri(orderDetail.getSeri().getName());
        productOrderDTO.setMemory(orderDetail.getMemory().getName());
        productOrderDTO.setColor(orderDetail.getColor().getName());
        productOrderDTO.setId(product.getId());
        productOrderDTO.setName(product.getName());
        productOrderDTO.setPrice(product.getPrice());
        productOrderDTO.setDescription(product.getDescription());
        productOrderDTO.setImage(product.getImages().get(defaultNum).getName());

        return productOrderDTO;
    }

    /**
     * Method admin delete Order by Id
     *
     * @param id
     * @return DELETED
     * @author TuongVi
     */
    public Boolean deleteOrder(long id) {
        int idStatus = 5; //idStatus = 5 => name = Deleted

        Orders order = orderRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));

        // idStatus = 5 => Order deleted => data not found
        if (order.getStatus().getId() == idStatus) {

            throw new ResourceNotFoundException(Collections.singletonMap("id", id));
        }

        Status status = statusRepository.findById(idStatus).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", id)));


        order.setStatus(status);

        orderRepository.saveAndFlush(order);

        return true;

    }


    /**
     * Method update orders
     *
     * @param order_id
     * @param orderUpdateDTO
     * @return Returns orderDetailsDTO if the order is updated successful
     * @Author NgoHoangKhacTuong
     */
    public OrderDetailsDTO update(long order_id, OrderUpdateDTO orderUpdateDTO) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
                        null, null)));

        long userID = user.getId();

        Orders oldOrder = orderRepository.findById(order_id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", order_id))
        );

        // Check if the authenticated user is authorized to update this order
        if (oldOrder.getUser().getId() != userID) {
            throw new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
                    null, null));
        } else {

            Orders updatedOrder = orderMapper.toOrderUpdateEntity(orderUpdateDTO);
            updatedOrder.setId(oldOrder.getId());
            updatedOrder.setUser(user);
            Date date = oldOrder.getReceiveDate();
            updatedOrder.setReceiveDate(oldOrder.getReceiveDate());

            Promotion updatedPromotion = promotionRepository.findById(orderUpdateDTO.getIdPromotion()).orElseThrow(
                    () -> new ResourceNotFoundException(Collections.singletonMap("Promotion ID",
                            orderUpdateDTO.getIdPromotion()))
            );
            updatedOrder.setPromotion(updatedPromotion);
            updatedOrder.setStatus(oldOrder.getStatus());

            // Update order payment method
            // Fetch the corresponding PaymentMethod entity using the payment method name from the orderUpdateDTO
            if (!oldOrder.getPaymentMethod().getName().equals(EPaymentMethod.Cash.toString()) &&
                    orderUpdateDTO.getPaymentMethodDTO().equals(EPaymentMethod.Cash.toString())) {
                throw new IllegalStateException("Update not allowed for non-cash payment method.");
            } else {

                updatedOrder.setPaymentMethod(paymentMethodRepository.findByName(
                        orderUpdateDTO.getPaymentMethodDTO())
                );
            }

            // Update order payment status based on payment method
            // If the payment method is not "Cash", set the payment status to true (payment made), otherwise set it to false
            updatedOrder.setPaymentStatus(!orderUpdateDTO.getPaymentMethodDTO().equals(EPaymentMethod.Cash.toString()));

            BigDecimal total = new BigDecimal(ENum.ZERO.getValue());
            for (ProductOrderDTO productOrderDTO : orderUpdateDTO.getOrderProductDTOList()) {
                total = total.add(productOrderDTO.getPrice());
            }
            total = totalProduct(total, updatedPromotion.getDiscount(), updatedPromotion.getTotalPurchase(),
                    updatedPromotion.getMaxGet());
            updatedOrder.setTotal(total);

            OrderDTO updatedOrderDTO = orderMapper.toDTO(orderRepository.save(updatedOrder));

            List<OrderDetails> oldOrderDetails = orderDetailRepository.findAllByOrderId(order_id);
            Long id = oldOrderDetails.get(0).getId();
            if (oldOrderDetails.size() > 0) {
                oldOrderDetails.forEach(oldOrderDetail -> {
                    orderDetailRepository.deleteById(oldOrderDetail.getId());
                });

            }
            String tableName = "order_details";
            resetAutoIncrement(tableName, id);

            OrderDetailsDTO newOrderDetailDTO = new OrderDetailsDTO();
            newOrderDetailDTO.setOrderDTO(updatedOrderDTO);
            newOrderDetailDTO.setQuantity(orderUpdateDTO.getOrderProductDTOList().size());
            newOrderDetailDTO.setOrderProductDTOList(orderUpdateDTO.getOrderProductDTOList());

            for (int i = 0; i < orderUpdateDTO.getOrderProductDTOList().size(); i++) {

                String color = orderUpdateDTO.getOrderProductDTOList().get(i).getColor();
                String memory = orderUpdateDTO.getOrderProductDTOList().get(i).getMemory();
                String seri = orderUpdateDTO.getOrderProductDTOList().get(i).getSeri();

                OrderDetails orderDetails = orderMapper.toDetailEnity(newOrderDetailDTO);
                orderDetails.setQuantity(ENum.ONE.getValue());

                orderDetails.setAddress(addressRepository.findAddressById(orderUpdateDTO.getIdAddress())
                        .orElse(addressRepository.findDefaultAddress(userID)));

                orderDetails.setColor(
                        colorRepository.findColorByNameAndProductId(
                                color,
                                orderUpdateDTO.getOrderProductDTOList().get(i).getId()).orElseThrow(
                                () -> new ValidationException(Collections.singletonMap("color name",
                                        color)))
                );

                orderDetails.setMemory(
                        memoryRepository.findMemoryByNameAndProductId(
                                memory,
                                orderUpdateDTO.getOrderProductDTOList().get(i).getId()).orElseThrow(
                                () -> new ValidationException(Collections.singletonMap("memory name",
                                        memory)))
                );

                orderDetails.setSeri(
                        seriRepository.findSeriByNameAndProductId(
                                seri,
                                orderUpdateDTO.getOrderProductDTOList().get(i).getId()).orElseThrow(
                                () -> new ValidationException(Collections.singletonMap("seri name",
                                        seri)))
                );
                orderDetails.setAddress(addressRepository.findAddressById(
                        orderUpdateDTO.getIdAddress()).orElse(addressRepository.findDefaultAddress(userID)));


                orderDetailRepository.save(orderDetails);
            }
            return newOrderDetailDTO;
        }
    }

    private void resetAutoIncrement(String tableName, long nextId) {
        String query = "ALTER TABLE " + tableName + " AUTO_INCREMENT = " + nextId;
        entityManager.createNativeQuery(query).executeUpdate();
    }

    /**
     * Method delete Order by Customer
     *
     * @param orderId
     * @return Return true: Delete Successfully!
     * @author phuocduc
     */
    @Override
    public Boolean deleteOrderByCustomer(long orderId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen", null, null)));

        int idStatus = 5; //idStatus = 5 => name = Deleted

        Orders order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", orderId)));

        // idStatus = 5 => Order deleted => data not found
        if (order.getStatus().getId() == idStatus) {
            throw new ResourceNotFoundException(Collections.singletonMap("id", orderId));
        }

        //Check user has this order, yes or no?
        if (!order.getUser().equals(user)) {
            throw new CannotDeleteException(String.format("Order Id: %s", orderId));
        }

        Status status = statusRepository.findById(idStatus).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", idStatus)));

        //Only cash payment method is allowed to delete
        long paymentMethodId = order.getPaymentMethod().getId();

        if (paymentMethodId != EPaymentMethod.cashPaymentMethod) {
            throw new CannotDeleteException(String.format("Payment Method: %s", order.getPaymentMethod().getName()));
        }

        order.setStatus(status);

        orderRepository.save(order);

        return true;
    }


    /**
     * Method user cancelOrder
     *
     * @param idOrder
     * @return true
     * @author TuongVi
     */

    public Boolean cancelOrder(long idOrder) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen", null, null)));


        Orders order = orderRepository.findById(idOrder).orElseThrow(
                () -> new ValidationException(Collections.singletonMap("id order",
                        idOrder)));

        if (!order.getUser().equals(user)) {
            throw new BadCredentialsException("Can't cancel");
        }

        Date curDate = java.sql.Timestamp.valueOf(LocalDateTime.now());
        if (curDate.after(order.getReceiveDate()))
            throw new BadCredentialsException("overdue");
        Status status = statusRepository.findByName(Estatus.Cancel.toString())
                .orElseThrow(() -> new ValidationException(Collections.singletonMap("id order",
                        Estatus.Cancel.toString())));
        order.setStatus(status);

        orderRepository.save(order);

        return true;
    }
}


