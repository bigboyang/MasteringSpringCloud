package com.example.ordersservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload {
    // DB 컬럼 내용과 일치해야함
    private String order_id;
    private String user_id;
    private String product_id;
    private int qty;
    private int total_price;
    private int unit_price;
}
