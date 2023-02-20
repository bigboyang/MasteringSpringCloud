package com.example.gatewayservice.dao;

import com.example.gatewayservice.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT user_rep_id FROM users WHERE user_rep_id = #{user_rep_id}")
    UserDTO getUserInfo(String user_rep_id);
}
