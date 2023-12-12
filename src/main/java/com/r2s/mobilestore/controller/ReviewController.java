package com.r2s.mobilestore.controller;

import com.r2s.mobilestore.constant.ApiURL;
import com.r2s.mobilestore.constant.PageDefault;
import com.r2s.mobilestore.data.dto.ReviewDTO;
import com.r2s.mobilestore.service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiURL.REVIEW)
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private MessageSource messageSource;

    /**
     * Method create review products
     *
     * @param reviewDTO
     * @return Returns an "ok" response if the address update is successful
     * @author ngohoangkhactuong
     */
    @PostMapping("")
    public ResponseEntity<?> createReview(@RequestBody ReviewDTO reviewDTO) {

        return new ResponseEntity<>(this.reviewService.create(reviewDTO), HttpStatus.CREATED);
    }

    /**
     * Method update review product
     *
     * @param reviewID
     * @param reviewDTO
     * @return Return a review product if update is successful
     * @author ngohoangkhactuong
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{reviewID}")
    public ResponseEntity<?> updateReview(
            @PathVariable long reviewID,
            @RequestBody ReviewDTO reviewDTO) {

        return new ResponseEntity<>(this.reviewService.update(reviewID, reviewDTO), HttpStatus.CREATED);
    }

    /**
     * Method delete review
     *
     * @param id
     * @return "DELETE" if delete is success
     * @author ngohoangkhactuong
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {

        this.reviewService.deleteById(id);

        return ResponseEntity.ok(messageSource.getMessage("success.deleted", null, null));
    }

    /**
     * Method get all and get one by product id review
     *
     * @param id
     * @return one or all review
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable long id,
                                                @RequestParam(defaultValue = PageDefault.NO) int no,
                                                @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {
        return ResponseEntity.ok(reviewService.getById(id, no, limit));
    }




    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("")
    public ResponseEntity<?> getAllReviews(@RequestParam(defaultValue = PageDefault.NO) int no,
                                                  @RequestParam(defaultValue = PageDefault.LIMIT) int limit) {

        return new ResponseEntity<>(
                this.reviewService.getAllPagination(no, limit), HttpStatus.ACCEPTED);
    }


}
