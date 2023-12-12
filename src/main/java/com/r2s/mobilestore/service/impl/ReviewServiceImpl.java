package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.common.MessageResponse;
import com.r2s.mobilestore.data.dto.PaginationDTO;
import com.r2s.mobilestore.data.dto.ReviewDTO;
import com.r2s.mobilestore.data.dto.ShowReviewDTO;
import com.r2s.mobilestore.data.entity.Product;
import com.r2s.mobilestore.data.entity.Review;
import com.r2s.mobilestore.data.entity.User;
import com.r2s.mobilestore.data.mapper.ReviewMapper;
import com.r2s.mobilestore.data.repository.ProductRepository;
import com.r2s.mobilestore.data.repository.ReviewRepository;
import com.r2s.mobilestore.data.repository.UserRepository;
import com.r2s.mobilestore.exception.InternalServerErrorException;
import com.r2s.mobilestore.exception.ResourceNotFoundException;
import com.r2s.mobilestore.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Objects;

/**
 * @author NguyenTienDat
 */
@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewReposiroty;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private MessageSource messageSource;

    /**
     * Method create review product
     *
     * @param reviewDTO
     * @return MessageResponse(" created review successfully ")
     * @author ngohoangkhactuong
     */
    @Override
    public MessageResponse create(ReviewDTO reviewDTO) {
        if (Objects.isNull(reviewDTO)) {
            throw new ResourceNotFoundException(Collections.singletonMap("Creation review ", reviewDTO));
        }

        Review review = reviewMapper.toEntity(reviewDTO);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
                        null, null)));
        review.setUser(user);


        int maxRating = 5;
        if (review.getRating() > maxRating) {
            return new MessageResponse(HttpServletResponse.SC_EXPECTATION_FAILED, "rating must be <= 5", null);
        }

        reviewReposiroty.save(review);



        return new MessageResponse(HttpServletResponse.SC_OK, "created review successfully", null);
    }

    /**
     * Method update review product
     *
     * @param reviewID
     * @param reviewDTO
     * @return Return a review product if update is successful
     * @author ngohoangkhactuong
     */
    @Override
    public ReviewDTO update(long reviewID, ReviewDTO reviewDTO) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
                        null, null)));

        long userID = user.getId();
        Review oldReview = reviewReposiroty.findById(reviewID).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", reviewID)
                ));

        if (oldReview.getUser().getId() != userID) {
            throw new InternalServerErrorException(messageSource.getMessage("error.userAuthen",
                    null, null));
        } else {

            Review updatedReview = reviewMapper.toEntity(reviewDTO);

            updatedReview.setId(oldReview.getId());
            updatedReview.setProduct(oldReview.getProduct());
            updatedReview.setUser(user);
            updatedReview.setStatus(true);

            int maxRating = 5;

            // check if rating of review is valid.
            if (updatedReview.getRating() > maxRating) {
                throw new InternalServerErrorException(messageSource.getMessage("rating must be <= 5", null, null));
            }

            reviewReposiroty.save(updatedReview);

            return reviewMapper.toDTO(updatedReview);
        }
    }

    /**
     * Method delete review
     *
     * @param id
     * @return true if delete is success
     * @author ngohoangkhactuong
     */
    @Override
    public boolean deleteById(long id) {

        Review review = reviewReposiroty.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", id))
        );

        if (!review.isStatus()) {
            throw new ResourceNotFoundException(Collections.singletonMap("id", id));
        }

        review.setStatus(false);
        this.reviewReposiroty.save(review);

        return true;
    }

    @Override
    public PaginationDTO getById(long id, int no, int limit) {

        Page<ShowReviewDTO> page = this.reviewReposiroty.findByProductIdCustomer(id,
                PageRequest.of(no, limit)).map(item -> reviewMapper.toShowDTO(item));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(),
                page.getTotalElements(), page.getSize(),
                page.getNumber());
    }

    @Override
    public PaginationDTO getAllPagination(int no, int limit) {

        Page<ShowReviewDTO> page = this.reviewReposiroty.findAllByAdmin(
                PageRequest.of(no, limit)).map(item -> reviewMapper.toShowDTO(item));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(),
                page.getTotalElements(), page.getSize(),
                page.getNumber());
    }
}
