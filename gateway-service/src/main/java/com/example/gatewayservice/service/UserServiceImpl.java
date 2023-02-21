package com.example.gatewayservice.service;

import com.example.gatewayservice.dao.UserMapper;
import com.example.gatewayservice.dto.CustDTO;
import com.example.gatewayservice.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserMapper userMapper;

    public UserDTO getUserInfo(String user_rep_id) {
        return userMapper.getUserInfo(user_rep_id);
    }

    public List<CustDTO> getCustIdList(String user_rep_id) {
        return userMapper.getCustIdList(user_rep_id);
    }

}
