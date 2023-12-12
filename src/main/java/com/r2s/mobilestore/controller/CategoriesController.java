package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.constant.PageDefault;
import com.r2s.mobilestore.data.dto.CategoriesDTO;
import com.r2s.mobilestore.service.CategoriesService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiURL.CATEGORIES)
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;
    @Autowired
    private MessageSource messageSource;

    /**
     * Method get all and get one by id categories
     *
     * @param id
     * @return one or all categories
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoriesById(@PathVariable long id) {
        return ResponseEntity.ok(categoriesService.getById(id));
    }

    @GetMapping("")
    public ResponseEntity<?> getAllCategories(@RequestParam(defaultValue = PageDefault.NO) int no,
                                              @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return new ResponseEntity<>(
                this.categoriesService.getAllPagination(no, limit), HttpStatus.ACCEPTED);
    }

    /**
     * Method create categories of products
     *
     * @param categories
     * @return Returns an "ok" response if the address update is successful
     */

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CategoriesDTO categories) {

        return ResponseEntity.ok(this.categoriesService.create(categories));
    }

    /**
     * Method update category
     *
     * @param id
     * @param categoriesDTO
     * @return CategoriesDTO if update success
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id,
                                    @RequestBody CategoriesDTO categoriesDTO) {

        return ResponseEntity.ok(this.categoriesService.update(id, categoriesDTO));
    }

    /**
     * Method delete ctegory by id
     *
     * @param id
     * @return
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {
        this.categoriesService.deleteById(id);

        return ResponseEntity.ok(messageSource.getMessage("success.deleted", null, null));
    }
}
