package com.example.gatewayservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustDTO {

    public CustDTO() {
        // 기본 생성자
    }

    public CustDTO(String user_rep_id, String cust_id) {
        this.user_rep_id = user_rep_id;
        this.cust_id = cust_id;
    }
    String user_rep_id;
    String cust_id;
}
