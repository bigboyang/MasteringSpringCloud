package com.example.gatewayservice.dao;

import com.example.gatewayservice.dto.CustDTO;
import com.example.gatewayservice.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT user_rep_id, role FROM users WHERE user_rep_id = #{user_rep_id}")
    UserDTO getUserInfo(String user_rep_id);

    @Select("SELECT cust_id FROM cust_info WHERE user_rep_id = #{user_rep_id}")
    List<CustDTO> getCustIdList(String user_rep_id);
}
