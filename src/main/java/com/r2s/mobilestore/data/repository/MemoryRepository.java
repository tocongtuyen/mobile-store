package com.r2s.mobilestore.data.repository;

import com.r2s.mobilestore.data.entity.Memory;
import com.r2s.mobilestore.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Integer> {

    @Query(value = "SELECT m FROM Memory m WHERE m.product.id =:#{#product.id} AND m.name =:name")
    boolean findByNameAndProductId(String name, @Param("product") Product product);

    @Query(value = "SELECT m FROM Memory m WHERE m.product.id =:id and m.status= 1")
    List<Memory> findByProductId(long id);

    @Query("SELECT m FROM Memory m WHERE m.name = :memoryName AND m.product.id = :productId")
    Optional<Memory> findMemoryByNameAndProductId(@Param("memoryName") String memoryName, @Param("productId") Long productId);

}
