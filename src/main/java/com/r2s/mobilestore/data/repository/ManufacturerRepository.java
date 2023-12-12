package com.r2s.mobilestore.data.repository;

import com.r2s.mobilestore.data.entity.Manufacturer;
import com.r2s.mobilestore.data.entity.PaymentMethod;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author NguyenTienDat
 */
@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

    boolean existsByName(String name);

    List<Manufacturer> findByStatusIsTrue();

    Optional<Manufacturer> findById(long id);
}
