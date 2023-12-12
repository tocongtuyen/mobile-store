package com.r2s.mobilestore.data.repository;

import com.r2s.mobilestore.data.entity.Technical;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author NguyenTienDat
 */
@Repository
public interface TechnicalRepository extends JpaRepository<Technical, Integer> {


    boolean existsByName(String name);
    
    Optional<Technical> findById(int id);

}
