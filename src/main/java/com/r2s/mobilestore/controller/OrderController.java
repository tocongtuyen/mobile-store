package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.constant.PageDefault;
import com.r2s.mobilestore.data.dto.oder.OrderCreationDTO;
import com.r2s.mobilestore.data.dto.oder.OrderUpdateDTO;
import com.r2s.mobilestore.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(ApiURL.ORDER)
public class OrderController {
    @Autowired
    private OrderService service;

    /**
     * Method create order by user
     *
     * @param orderCreationDTO
     * @return Returns an "ok" response if the address update is successful
     * @Author VoTien
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Customer')")
    @Transactional
    @PostMapping("")
    public ResponseEntity<?> createOrders(@RequestBody OrderCreationDTO orderCreationDTO) {
        return new ResponseEntity<>(this.service.create(orderCreationDTO), HttpStatus.CREATED);
    }

    /**
     * Method update orders
     *
     * @param order_id
     * @param orderUpdateDTO
     * @return Returns an order if the order is updated successful
     * @Author NgoHoangKhacTuong
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Customer')")
    @Transactional
    @PutMapping("/{order_id}")
    public ResponseEntity<?> updatedOrders(@PathVariable long order_id, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        return new ResponseEntity<>(this.service.update(order_id, orderUpdateDTO), HttpStatus.CREATED);
    }

    /**
     * Method set order status to "Canceled" by user using order id
     *
     * @param id
     * @return Returns an "DELETED" response if the order is deleted successful
     * @Author AnhTuan
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Customer')")
    @Transactional
    @DeleteMapping("/delete-order-by-user/{id}")
    public ResponseEntity<?> deleteOrderUser(@PathVariable long id) {

        this.service.deleteOrderByIdUser(id);

        return ResponseEntity.ok("DELETED");
    }

    /**
     * Method show list Order by User
     *
     * @return Return the list of orders by the user
     * @Author TuongVi
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Customer')")
    @GetMapping("/user")
    public ResponseEntity<?> showOrderByUser(@RequestParam(defaultValue = PageDefault.NO) int no,
                                             @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return new ResponseEntity<>(this.service.showOrderByUser(no, limit), HttpStatus.OK);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @GetMapping("")
    public ResponseEntity<?> showAllOrderByAdmin(@RequestParam(defaultValue = PageDefault.NO) int no,
                                                 @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return new ResponseEntity<>(this.service.showAllOrderByAdmin(no, limit), HttpStatus.OK);
    }

    /**
     * Method show details Order by User
     *
     * @param orderId
     * @return Return the list of  details orders by user
     * @Author TuongVi
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Customer')")
    @Transactional
    @GetMapping("/user/detail/{orderId}")
    public ResponseEntity<?> showDetailsOrderByUser(@PathVariable long orderId) {

        return new ResponseEntity<>(this.service.getOrderDetailsDTO(orderId), HttpStatus.OK);
    }


    /**
     * Method show list Order by Admin
     *
     * @return Return the list of orders by admin
     * @Author TuongVi
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @Transactional
    @GetMapping("/detail/{orderId}")
    public ResponseEntity<?> showDetailsOrderByAdmin(@PathVariable long orderId) {

        return new ResponseEntity<>(this.service.getOrderDetailsDTO(orderId), HttpStatus.CREATED);
    }

    /**
     * Method delete Order by Admin
     *
     * @param orderId
     * @return Return true: Delete Successfully!
     * @author tuongvi
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @Transactional
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteOrderAdmin(@PathVariable long orderId) {

        return new ResponseEntity<>(this.service.deleteOrder(orderId), HttpStatus.OK);
    }

    /**
     * Method cancel Order by User
     *
     * @param orderId
     * @return Return true:  Successfully!
     * @author tuongvi
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Customer')")
    @Transactional
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable long orderId) {

        return new ResponseEntity<>(this.service.cancelOrder(orderId), HttpStatus.OK);
    }
}
