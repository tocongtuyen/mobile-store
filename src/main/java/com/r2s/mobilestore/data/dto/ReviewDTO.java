package com.r2s.mobilestore.data.dto;

import com.r2s.mobilestore.data.dto.user.UserDTO;
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
public class ReviewDTO {

    private String user_name;
    private long product_id;
    private String comment;
    private int rating;
    private boolean status;
}
