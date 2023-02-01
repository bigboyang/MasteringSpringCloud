package com.example.userservice.client;

import com.example.userservice.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "orders-service") // 유레카 micro-service 이름
public interface OrderServiceClient {

    @GetMapping("/orders-service/{userId}/orders")
    List<ResponseOrder> getOrders(@PathVariable String userId);

}
