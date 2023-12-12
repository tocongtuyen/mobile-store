package com.r2s.mobilestore.service.impl;

import com.r2s.mobilestore.common.enumeration.Estatus;
import com.r2s.mobilestore.data.dto.StatusDTO;
import com.r2s.mobilestore.data.entity.Status;
import com.r2s.mobilestore.data.mapper.StatusMapper;
import com.r2s.mobilestore.data.repository.StatusRepository;
import com.r2s.mobilestore.exception.ResourceNotFoundException;
import com.r2s.mobilestore.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StatusServiceImpl implements StatusService {

    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private StatusMapper statusMapper;

    @PostConstruct
    public void init() {
        List<Status> statusToSave = new ArrayList<>();
        for (Estatus eStatus : Estatus.values()) {
            if (!statusRepository.existsByName(eStatus.toString())) {
                Status status = new Status();
                status.setName(eStatus.toString());
                statusToSave.add(status);
            }
        }
        if (!statusToSave.isEmpty()) {
            statusRepository.saveAll(statusToSave);
        }
    }

    @Override
    public StatusDTO findByName(String name) {
        return statusMapper.toDTO(
                statusRepository.findByName(name)
                        .orElseThrow(() -> new ResourceNotFoundException(Collections.singletonMap("Role name", name))));
    }
}
