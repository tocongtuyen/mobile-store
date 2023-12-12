package com.r2s.mobilestore.service;

import com.r2s.mobilestore.data.dto.RoleDTO;

public interface RoleService {

    RoleDTO findByName(String name);
}
