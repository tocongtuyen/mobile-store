package com.r2s.mobilestore.data.repository;

import com.r2s.mobilestore.data.entity.Color;
import com.r2s.mobilestore.data.entity.Image;
import com.r2s.mobilestore.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query(value = "SELECT i FROM Image i WHERE i.product.id =:#{#product.id} AND i.name =:name")
    boolean findByNameAndProductId(String name, @Param("product") Product product);

    @Query(value = "SELECT i FROM Image i WHERE i.product.id =:id and i.status= 1")
    List<Image> findByProductId(long id);
}
