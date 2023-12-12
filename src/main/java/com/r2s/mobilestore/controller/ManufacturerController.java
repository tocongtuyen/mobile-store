package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.constant.PageDefault;
import com.r2s.mobilestore.data.dto.ManufacturerDTO;
import com.r2s.mobilestore.service.ManufacturerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiURL.MANUFACTURER)
public class ManufacturerController {

    @Autowired
    private ManufacturerService manufacturerService;
    @Autowired
    private MessageSource messageSource;

    /**
     * Method get all and get one by id manufacturer
     *
     * @param id
     * @return one or all manufacturer
     */
    @GetMapping("{id}")
    public ResponseEntity<?> getManufacturerById(@PathVariable long id){
        return ResponseEntity.ok(manufacturerService.getById(id));
    }
    
    @GetMapping("")
    public ResponseEntity<?> getAllManufacturers(@RequestParam(defaultValue = PageDefault.NO) int no,
                                         @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return new ResponseEntity<>(
        		this.manufacturerService.getAllPagination(no, limit), HttpStatus.ACCEPTED);
    }

    /**
     * Method create manufacturer for products
     *
     * @param meManufacturerDTO
     * @return Returns an "ok" response if the address update is successful
     * @Author
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody ManufacturerDTO meManufacturerDTO) {

        return ResponseEntity.ok(this.manufacturerService.create(meManufacturerDTO));
    }

    /**
     * Method update manufacture
     *
     * @param id
     * @param manufacturerDTO
     * @return manufacturerDTO if update success
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id,
                                    @RequestBody ManufacturerDTO manufacturerDTO) {

        return ResponseEntity.ok(this.manufacturerService.update(id, manufacturerDTO));
    }

    /**
     * Method delete manufacturer
     *
     * @param id
     * @return return "Xóa thành công" if delete success
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {
    	
    	this.manufacturerService.deleteById(id);

        return ResponseEntity.ok(messageSource.getMessage("success.deleted", null, null));
    }
}
