package com.r2s.mobilestore.data.repository;

import com.r2s.mobilestore.data.entity.PaymentMethod;
import com.r2s.mobilestore.data.entity.Product;
import com.r2s.mobilestore.data.entity.ProductTech;
import com.r2s.mobilestore.data.entity.Seri;
import com.r2s.mobilestore.data.entity.Seri;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author NguyenTienDat
 */
@Repository
public interface SeriRepository extends JpaRepository<Seri, Long> {

    boolean existsByName(String name);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Seri s WHERE s.product.id = :productId")
    void deleteByProduct(long productId);

    @Query(value = "SELECT i FROM Seri i WHERE i.product.id =:#{#product.id} AND i.name =:name")
    boolean findByNameAndProductId(String name, @Param("product") Product product);

    @Query(value = "SELECT s FROM Seri s WHERE s.product.id =:id and s.status= 1")
    List<Seri> findByProductId(long id);
    Optional<Seri> findByName(String name);

    @Query("SELECT s FROM Seri s WHERE s.name = :seriName AND s.product.id = :productId")
    Optional<Seri> findSeriByNameAndProductId(@Param("seriName") String seriName, @Param("productId") Long productId);
    
    Optional<Seri> findById(long id);
}
