package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.data.dto.ColorDTO;
import com.r2s.mobilestore.data.dto.ColorProductDTO;
import com.r2s.mobilestore.service.ColorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiURL.COLOR)
public class ColorController {

    @Autowired
    private ColorService colorService;
    @Autowired
    private MessageSource messageSource;

    /**
     * Method create color
     *
     * @param colorDTO
     * @return colorDTO
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody ColorProductDTO colorDTO ){
        return ResponseEntity.ok(colorService.create(colorDTO));
    }

    /**
     * Method update color
     *
     * @param id
     * @param colorDTO
     * @return colorDTO if update success
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id,
                                    @RequestBody ColorProductDTO colorDTO) {

        return ResponseEntity.ok(colorService.update(id, colorDTO));
    }

    /**
     * Method delete color
     *
     * @param id
     * @return "Xóa thành công" if delete success
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable int id) {
        this.colorService.delete(id);
        return ResponseEntity.ok(messageSource.getMessage("success.deleted", null, null));
    }
}
