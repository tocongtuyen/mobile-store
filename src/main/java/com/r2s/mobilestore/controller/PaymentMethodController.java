package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.constant.PageDefault;
import com.r2s.mobilestore.data.dto.PaymentMethodDTO;
import com.r2s.mobilestore.service.PaymentMethodService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(ApiURL.METHOD)
public class PaymentMethodController {
    @Autowired
    private PaymentMethodService paymentMethodService;
    @Autowired
    private MessageSource messageSource;

    /**
     * Method get all and get one by id paymentMethod
     *
     * @param id
     * @return one or all paymentMethod
     */
    @GetMapping("{id}")
    public ResponseEntity<?> getPaymentMethodById(@PathVariable long id){
        return ResponseEntity.ok(paymentMethodService.getById(id));
    }
    
    @GetMapping("")
    public ResponseEntity<?> getAllPaymentMethods(@RequestParam(defaultValue = PageDefault.NO) int no,
                                         @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return new ResponseEntity<>(
        		this.paymentMethodService.getAllPagination(no, limit), HttpStatus.ACCEPTED);
    }

    /**
     * Method create payment
     *
     * @param paymentMethodDTO
     * @return Returns an "ok" response if the address update is successful
     * @Author
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody PaymentMethodDTO paymentMethodDTO) {

        return ResponseEntity.ok(this.paymentMethodService.create(paymentMethodDTO));
    }

    /**
     * Method update PaymentMethod
     *
     * @param id
     * @param paymentMethodDTO
     * @return PaymentMethodDTO if update success
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id,
                                    @RequestBody PaymentMethodDTO paymentMethodDTO) {

        return ResponseEntity.ok(this.paymentMethodService.update(id, paymentMethodDTO));
    }

    /**
     * Method delete paymentMethod
     *
     * @param id
     * @return return "Xóa thành công" if delete success
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {

        this.paymentMethodService.deleteById(id);

        return ResponseEntity.ok(messageSource.getMessage("success.deleted", null, null));
    }
}
