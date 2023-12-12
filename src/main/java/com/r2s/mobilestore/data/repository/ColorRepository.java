package com.r2s.mobilestore.data.repository;

import com.r2s.mobilestore.data.dto.ColorProductDTO;
import com.r2s.mobilestore.data.entity.Color;
import com.r2s.mobilestore.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/**
 * @author NguyenTienDat
 */
@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {

    @Query(value = "SELECT c FROM Color c WHERE c.product.id =:id and c.status= 1")
    List<Color> findByProductId(long id);

    @Query("SELECT c FROM Color c WHERE c.name = :colorName AND c.product.id = :productId")
    Optional<Color> findColorByNameAndProductId(@Param("colorName") String colorName, @Param("productId") long productId);

    boolean existsByName (String name);

    @Query("SELECT c FROM Color c WHERE c.name = :colorName AND c.product.id = :productId")
    List<Color> findColorByNameAndProId(@Param("colorName") String colorName, @Param("productId") Long productId);

    Optional<Color> findById(int id);
}
