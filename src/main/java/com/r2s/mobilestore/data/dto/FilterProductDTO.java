package com.r2s.mobilestore.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.r2s.mobilestore.common.enumeration.ENum;
import com.r2s.mobilestore.constant.FilterDefault;
import com.r2s.mobilestore.constant.PriceDefault;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterProductDTO {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(defaultValue = FilterDefault.MANUFACTURER_ID)
    private int manufactureId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(defaultValue = FilterDefault.CATEGORY_ID)
    private int categoryId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(defaultValue = FilterDefault.KEYWORD)
    private String keyword;

    private BigDecimal lowerPrice;

    private BigDecimal higherPrice;

}
