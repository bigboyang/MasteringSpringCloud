package com.example.gatewayservice.service;

import com.example.gatewayservice.dao.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserMapper userMapper;

}
