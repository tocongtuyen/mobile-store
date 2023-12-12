package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.constant.PageDefault;
import com.r2s.mobilestore.constant.PriceDefault;
import com.r2s.mobilestore.data.dto.FilterProductDTO;
import com.r2s.mobilestore.data.dto.product.ProductDTO;
import com.r2s.mobilestore.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: NguyenTienDat, VoTien, huuduc, ngohoangkhactuong, tuongvi
 */
@RestController
@RequestMapping(ApiURL.PRODUCT)
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private MessageSource messageSource;

    /**
     * Method get new product
     *
     * @return ShowProductDTO
     */

    @GetMapping("/new")
    public ResponseEntity<?> getNewProduct() {
        return ResponseEntity.ok(this.productService.findNewProduct());
    }

    /**
     * Method create products
     *
     * @param productDTO
     * @param fileImages
     * @return ResponseEntity<ProductDTO>
     * @author NguyenTienDat
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createProduct(@Valid @RequestPart ProductDTO productDTO,
                                           @RequestPart(name = "fileImages", required = false)
                                           List<MultipartFile> fileImages) {

        return new ResponseEntity<>(this.productService.create(productDTO, fileImages), HttpStatus.CREATED);
    }

    /**
     * Method update products
     *
     * @param id
     * @param productDTO
     * @param fileImages
     * @return responseEntity(< ProductDTO >)
     * @author NguyenTienDat
     */

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateProduct(@PathVariable long id, @Valid @RequestPart ProductDTO productDTO,
                                           @RequestPart(name = "fileImages", required = false)
                                           List<MultipartFile> fileImages) {

        return new ResponseEntity<>(this.productService.update(id, productDTO, fileImages), HttpStatus.CREATED);
    }

    /**
     * method Search product following the page
     *
     * @param no
     * @param limit
     * @return responseEntity(List < ProductDTO >)
     * @author NguyenTienDat
     */
    @GetMapping("/active")
    public ResponseEntity<?> findAllActive(@RequestParam(defaultValue = PageDefault.NO) int no,
                                           @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return ResponseEntity.ok(productService.findAllActiveProduct(no, limit));
    }

    /**
     * Method Search product by keyword
     *
     * @param keyword
     * @return List<ShowProductDTO>
     * @Author VoTien
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/search-product")
    public ResponseEntity<?> searchProduct(@RequestParam(defaultValue = "") String keyword,
                                           @RequestParam(defaultValue = PageDefault.NO) int no,
                                           @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return ResponseEntity.ok().body(this.productService.searchProduct(keyword, no, limit));
    }


    /**
     * Method delete product by status
     *
     * @param id
     * @return DELETED
     * @author huuduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {

        this.productService.deleteById(id);

        return ResponseEntity.ok(messageSource.getMessage("success.deleted", null, null));
    }

    /**
     * Method show product by categories
     *
     * @param categoryId
     * @param no
     * @param limit
     * @return PaginationDTO (productDTO)
     * @author ngohoangkhactuong
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/show-product/{categoryId}")
    public ResponseEntity<?> showListProduct(@PathVariable Long categoryId,
                                             @RequestParam(defaultValue = PageDefault.NO) int no,
                                             @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return ResponseEntity.ok().body(this.productService.showListProductPagination(categoryId, no, limit));
    }

    /**
     * Method show detail and increase view
     *
     * @param id
     * @return productDTO
     * @author tuongvi
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> showProductDetail(@PathVariable long id) {

        return ResponseEntity.ok().body(this.productService.showDetailProduct(id));
    }


    /**
     * method search products by many params
     *
     * @param filterProductDTO
     * @param no
     * @param limit
     * @return PaginationDTO (productDTO)
     * @author NguyenTienDat
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/filter-product")
    public ResponseEntity<?> showListProduct(@RequestBody FilterProductDTO filterProductDTO,
                                             @RequestParam(defaultValue = PageDefault.NO) int no,
                                             @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {
        if (filterProductDTO.getLowerPrice() == null){
            filterProductDTO.setLowerPrice(new BigDecimal(PriceDefault.LOWER_PRICE));
        }
        if (filterProductDTO.getHigherPrice() == null)
        {
            filterProductDTO.setHigherPrice(new BigDecimal(PriceDefault.HIGHER_PRICE));
        }

        return ResponseEntity.ok().body(this.productService
                .showListProductFilter(filterProductDTO, no, limit));
    }


    /**
     * Method related products by category and manufacturer (default = 6)
     *
     * @param productId
     * @param quantity
     * @return List<ShowProductRelated>
     * @Author phuocduc
     */

//    @PreAuthorize("hasAuthority('Role_Customer')")
    @GetMapping("/related-product")
    public ResponseEntity<?> showRelatedProduct(@RequestParam(defaultValue = "0") long productId,
                                                @RequestParam(defaultValue = "6") int quantity
    ) {
        return ResponseEntity.ok().body(this.productService.showRelatedProduct(productId, quantity));
    }

    /**
     * Method show active product by category
     *
     * @param categoryId
     * @param no
     * @param limit
     * @return PaginationDTO (productDTO)
     * @author phuocduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/active-category/{categoryId}")
    public ResponseEntity<?> showActiveProductByCategory(@PathVariable long categoryId,
                                                         @RequestParam(defaultValue = PageDefault.NO) int no,
                                                         @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {
        return ResponseEntity.ok().body(this.productService.showActiveProductByCategory(categoryId, no, limit));
    }


    /**
     * Method show active product by manufacturer
     *
     * @param manufacturerId
     * @param no
     * @param limit
     * @return PaginationDTO (productDTO)
     * @author phuocduc
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/active-manufacturer/{manufacturerId}")
    public ResponseEntity<?> showActiveProductByManufacturer(@PathVariable long manufacturerId,
                                                             @RequestParam(defaultValue = PageDefault.NO) int no,
                                                             @RequestParam(defaultValue = PageDefault.LIMIT)
                                                             int limit) {
        return ResponseEntity.ok().body(this.productService.showActiveProductByManufacturer(manufacturerId, no, limit));
    }

    /**
     * Show active product by price
     *
     * @param lowerPrice
     * @param higherPrice
     * @param no
     * @param limit
     * @return ResponseEntity<ProductDTO>
     * @author NguyenTienDat
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/active-price")
    public ResponseEntity<?> showActiveProductByPrice(@RequestParam(defaultValue = PriceDefault.LOWER_PRICE)
                                                      BigDecimal lowerPrice,
                                                      @RequestParam(defaultValue = PriceDefault.HIGHER_PRICE)
                                                      BigDecimal higherPrice,
                                                      @RequestParam(defaultValue = PageDefault.NO) int no,
                                                      @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {
        return ResponseEntity.ok().body(this.productService.FilterProductsByPrice(lowerPrice, higherPrice, no, limit));
    }


}

