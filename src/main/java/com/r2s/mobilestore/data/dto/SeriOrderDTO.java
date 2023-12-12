package com.r2s.mobilestore.data.dto;

import com.r2s.mobilestore.data.dto.product.ProductOrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author NguyenTienDat
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SeriOrderDTO implements Serializable {

    private long id;

    private ProductOrderDTO productOrderDTO;

    private String name;
}
