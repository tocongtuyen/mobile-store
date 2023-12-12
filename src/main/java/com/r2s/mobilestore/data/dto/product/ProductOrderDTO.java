package com.r2s.mobilestore.data.dto.product;

import com.r2s.mobilestore.data.dto.ColorDTO;
import com.r2s.mobilestore.data.dto.MemoryDTO;
import com.r2s.mobilestore.data.dto.SeriDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderDTO {
    private long id;
    private BigDecimal price;
    private String name;
    private String description;
    private String memory;
    private String color;
    private String seri;

}
