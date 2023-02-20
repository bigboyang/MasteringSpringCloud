package com.example.gatewayservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiDTO {

    public ApiDTO() {
        // 기본 생성자
    }

    public ApiDTO(String url, String custgp_yn, String role_yn) {
        this.url = url;
        this.custgp_yn = custgp_yn;
        this.role_yn = role_yn;
    }

    String url;
    String custgp_yn;
    String role_yn;
}