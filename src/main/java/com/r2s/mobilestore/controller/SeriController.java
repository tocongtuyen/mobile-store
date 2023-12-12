package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.constant.PageDefault;
import com.r2s.mobilestore.data.dto.SeriDTO;
import com.r2s.mobilestore.data.dto.SeriProductDTO;
import com.r2s.mobilestore.service.SeriService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(ApiURL.SERI)
public class SeriController {
    @Autowired
    private SeriService seriService;

    /**
     * Method create seres
     *
     * @param seriDTO
     * @return
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody SeriProductDTO seriDTO) {

        return ResponseEntity.ok(this.seriService.create(seriDTO));
    }

    /**
     * Method update seri product
     *
     * @param seriID
     * @param seriProductDTO
     * @return Return a seri product if update is successful
     * @author ngohoangkhactuong
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PutMapping("/{seriID}")
    public ResponseEntity<?> update(
            @RequestBody SeriProductDTO seriProductDTO,
            @PathVariable long seriID) {

        return new ResponseEntity<>(this.seriService.update(seriID, seriProductDTO), HttpStatus.CREATED);
    }

    /**
     * Methor delete seri
     *
     * @param id
     * @return "DELETE" if delete is success
     * @author ngohoangkhactuong
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        this.seriService.deletedByID(id);

        return ResponseEntity.ok("info.deleteSeri");
    }
    
    /**
     * Method get all and get one by id seri
     *
     * @param id
     * @return one or all seri
     */
    @GetMapping("{id}")
    public ResponseEntity<?> getSeriById(@PathVariable long id){
        return ResponseEntity.ok(seriService.getById(id));
    }
    
    @GetMapping("")
    public ResponseEntity<?> getAllPaymentMethods(@RequestParam(defaultValue = PageDefault.NO) int no,
                                         @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return new ResponseEntity<>(
        		this.seriService.getAllPagination(no, limit), HttpStatus.ACCEPTED);
    }
}
