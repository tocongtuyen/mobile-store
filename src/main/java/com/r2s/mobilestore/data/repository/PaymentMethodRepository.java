package com.r2s.mobilestore.data.repository;

import com.r2s.mobilestore.data.entity.PaymentMethod;
import com.r2s.mobilestore.data.entity.ProductTech;

import java.util.Optional;

import org.hibernate.sql.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    boolean existsByName(String name);

    PaymentMethod findByName(String name);

    @Query("Select p from PaymentMethod p Where p.status = true and p.id = :id")

    Optional<PaymentMethod> findByIdPagement(long id);

    List<PaymentMethod> findByStatusIsTrue();

    @Query("Select p from PaymentMethod p Where p.status = true")
    Page<PaymentMethod> findAllPagment(Pageable pageable);

}
