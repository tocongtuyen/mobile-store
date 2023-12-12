package com.r2s.mobilestore.service;

import com.r2s.mobilestore.data.dto.StatusDTO;

public interface StatusService {
    StatusDTO findByName(String name);
}
