package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.data.dto.address.AddressDTO;
import com.r2s.mobilestore.service.AddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(ApiURL.ADDRESS)
public class AddressController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private MessageSource messageSource;


    /**
     * Method get by id address (customer)
     *
     * @param id
     * @return addressDTO
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<?> getByIdAddress(@PathVariable long id){
        return ResponseEntity.ok(addressService.getById(id));
    }


    /**
     * Method get all address by id user (customer)
     *
     * @return List addressDTO
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping("")
    public ResponseEntity<?> getAllAddress(){
        return ResponseEntity.ok(addressService.getAllAddress());
    }



    /**
     * Method create address of user
     *
     * @param addressDTO
     * @return Returns an "ok" response if the address update is successful
     * @Author :VoTien, TuongVi
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Customer')")
    @PostMapping("")
    public ResponseEntity<?> createAddress(@RequestBody AddressDTO addressDTO) {

        return new ResponseEntity<>(this.addressService.createAddress(addressDTO), HttpStatus.CREATED);
    }

    /**
     * Method update address of user
     *
     * @param addressDTO
     * @param id
     * @return Returns an "ok" response if the address update is successful
     * @Author VoTien, TuongVi
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Customer')")
    @PutMapping("/update-address/{id}")
    public ResponseEntity<?> updateAddress(@RequestBody AddressDTO addressDTO, @PathVariable long id) {

        return ResponseEntity.ok(addressService.updateAddress(addressDTO, id));
    }

    /**
     * Method delete address by id (controller)
     *
     * @param id
     * @return Return "DELETED" if delete address successful
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Customer')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {

        this.addressService.deleteAddress(id);

        return ResponseEntity.ok(messageSource.getMessage("success.deleted", null, null));
    }
}
