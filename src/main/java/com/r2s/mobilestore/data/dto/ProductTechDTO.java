package com.r2s.mobilestore.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author NguyenTienDat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTechDTO {

    private int id;
    private TechnicalDTO technicalDTO;
    private String info;
    private boolean isPrimary;

}
