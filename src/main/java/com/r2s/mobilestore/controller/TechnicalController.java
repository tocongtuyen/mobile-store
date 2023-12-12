package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.constant.PageDefault;
import com.r2s.mobilestore.data.dto.TechnicalDTO;
import com.r2s.mobilestore.service.TechnicalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiURL.TECHNICAL)
public class TechnicalController {

    @Autowired
    private TechnicalService technicalService;


    /**
     * Method create technical
     *
     * @param name
     * @return colorDTO
     */

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PostMapping("")
    public ResponseEntity<?> create(@RequestParam String name){
        return ResponseEntity.ok(technicalService.create(name));
    }


    /**
     * Method update technical
     *
     * @param id
     * @param technicalDTO
     * @return return technicalDTO if update success
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id,
                                    @RequestBody TechnicalDTO technicalDTO) {

        return ResponseEntity.ok(this.technicalService.update(id, technicalDTO));
    }
    
    /**
     * Method get all and get one by id technical
     *
     * @param id
     * @return one or all technical
     */
    @GetMapping("{id}")
    public ResponseEntity<?> getTechnicalById(@PathVariable int id){
        return ResponseEntity.ok(technicalService.getById(id));
    }
    
    @GetMapping("")
    public ResponseEntity<?> getAllTechnicals(@RequestParam(defaultValue = PageDefault.NO) int no,
                                         @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return new ResponseEntity<>(
        		this.technicalService.getAllPagination(no, limit), HttpStatus.ACCEPTED);
    }

}
