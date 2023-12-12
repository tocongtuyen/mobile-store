package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.constant.PageDefault;
import com.r2s.mobilestore.service.ProductTechService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiURL.PRODUCT_TECHNICAL)
public class ProductTechnicalController {

    @Autowired
    private ProductTechService productTechService;
    @Autowired
    private MessageSource messageSource;

    /**
     * Method update product_tech
     *
     * @param id
     * @param info
     * @return ProductTechDTO if update success
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id,
                                    @RequestParam String info) {

        return ResponseEntity.ok(productTechService.update(id, info));
    }

    /**
     * Method delete(set status) product_tech
     *
     * @param id
     * @return "DELETED" if delete success
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable int id) {

        this.productTechService.deleteById(id);

        return ResponseEntity.ok(messageSource.getMessage("success.deleted", null, null));
    }
    
    /**
     * Method get all and get one by id product_tech
     *
     * @param id
     * @return one product_tech or all product_tech
     */
    @GetMapping("{id}")
    public ResponseEntity<?> getProductTechnicalById(@PathVariable int id){
        return ResponseEntity.ok(productTechService.getById(id));
    }
    
    @GetMapping("")
    public ResponseEntity<?> getAllProductTechnicals(@RequestParam(defaultValue = PageDefault.NO) int no,
                                         @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return new ResponseEntity<>(
        		this.productTechService.getAllPagination(no, limit), HttpStatus.ACCEPTED);
    }
}
