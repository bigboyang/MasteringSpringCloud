package com.example.gatewayservice.service;

import com.example.gatewayservice.dao.ApiMapper;
import com.example.gatewayservice.dto.ApiDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiServiceImpl {

    private final ApiMapper apiMapper;

    public List<ApiDTO> getAllApis()  {
        System.out.println(apiMapper.getAllApis());
        return apiMapper.getAllApis();
    }
}
