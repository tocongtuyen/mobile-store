package com.r2s.mobilestore.data.dto.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressDTO {
    private String location;
    private String phoneReceiver;
    private String nameReceiver;
    private boolean defaults;
    private String type;
}
