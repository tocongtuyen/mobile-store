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
public class MemoryDTO implements Serializable {

    private int id;
    private String name;
}
