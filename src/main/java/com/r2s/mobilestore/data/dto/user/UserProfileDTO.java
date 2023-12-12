package com.r2s.mobilestore.data.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.r2s.mobilestore.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserProfileDTO implements Serializable {

    private String email;
    private String fullName;
    private int gender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DATE_FORMAT)
    private Date birthDay;

}
