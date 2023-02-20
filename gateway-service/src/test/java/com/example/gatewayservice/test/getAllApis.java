package com.example.gatewayservice.test;

import com.example.gatewayservice.dao.ApiMapper;
import com.example.gatewayservice.service.ApiServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class getAllApis {

    @Autowired
    private ApiServiceImpl apiService;
    @Autowired
    private ApiMapper apiMapper;
    @Test
    public void getAllApisTest() {
        // getAllApisTest
        apiService.getAllApis();
    }
}
