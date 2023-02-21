package com.example.gatewayservice.dao;

import com.example.gatewayservice.dto.ApiDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface ApiMapper {
    @Select("SELECT url, custgp_yn, role_yn FROM apis")
    List<ApiDTO> getAllApis();

    @Select("Select url, custgp_yn, role_yn FROM apis WHERE url = #{url}")
    ApiDTO getApi(String url);
}
