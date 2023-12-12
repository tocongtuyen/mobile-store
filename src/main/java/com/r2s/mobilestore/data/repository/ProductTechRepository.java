package com.r2s.mobilestore.data.repository;

import com.r2s.mobilestore.data.dto.TechnicalDTO;
import com.r2s.mobilestore.data.entity.Product;
import com.r2s.mobilestore.data.entity.ProductTech;
import com.r2s.mobilestore.data.entity.Promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTechRepository extends JpaRepository<ProductTech, Integer> {

    @Query(value = "SELECT i FROM ProductTech i " +
                   "WHERE i.product.id =:#{#product.id} " +
                   "AND i.info =:#{#technical.info} " +
                   "AND i.technical.id=:#{#technical.id}")
    boolean findByProductIdAndInfoAndTechnicalId(@Param("technical") TechnicalDTO technicalDTO, @Param("product") Product product);

    @Query(value = "SELECT pt FROM ProductTech pt WHERE pt.product.id =:id and pt.status= 1")
    List<ProductTech> findByProductId(long id);
    
    Optional<ProductTech> findById(int id);
}
