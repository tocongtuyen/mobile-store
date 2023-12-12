package com.r2s.mobilestore.data.repository;

import com.r2s.mobilestore.data.dto.CategoriesDTO;
import com.r2s.mobilestore.data.entity.Categories;
import com.r2s.mobilestore.data.entity.PaymentMethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author NguyenTienDat
 */
@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    boolean existsByName(String name);

    Optional<Object> findByName(String name);

    List<Categories> findByStatusIsTrue();

    Optional<Categories> findById(long id);
}
