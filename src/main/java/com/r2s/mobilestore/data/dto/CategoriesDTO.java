package com.r2s.mobilestore.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author NguyenTienDat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesDTO implements Serializable {

    private long id;
    private String name;
}
