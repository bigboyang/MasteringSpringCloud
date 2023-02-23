package com.example.gatewayservice.filter;

import com.example.gatewayservice.dto.ApiDTO;
import com.example.gatewayservice.dto.CustDTO;
import com.example.gatewayservice.dto.UserDTO;
import com.example.gatewayservice.service.ApiServiceImpl;
import com.example.gatewayservice.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    private final UserServiceImpl userService;
    private final ApiServiceImpl apiService;

    public CustomFilter(UserServiceImpl userService, ApiServiceImpl apiService) {
        super(Config.class);
        this.userService = userService;
        this.apiService = apiService;
    }

    /**
     * service에 따른 ip변경, 고객군 및 역할 매핑 후 다음 필터로 넘어감
     * */
    @Override
    public GatewayFilter apply(Config config) {

        // Custom Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
//            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom PRE filter : request id -> {}", request.getId());
            log.info("Custom PRE filter : request ip -> {}", request.getURI());
            log.info("Custom PRE filter : request ip -> {}", request.getHeaders().get("service_type"));
            log.info("Custom PRE filter : request ip -> {}", request.getHeaders().get("user_rep_id"));

            // 필터 필요없는 url들 제외
            if (request.getURI().getPath().contains("/login")
                    || request.getURI().getPath().contains("/token")
                    || request.getURI().getPath().contains("/users")
            ){
                String[] path = request.getURI().getPath().split("/");
                URI requestUri = exchange.getRequest().getURI();
                URI newUri = UriComponentsBuilder.fromUri(requestUri).host("127.0.0.1").port(8989)
                        .replacePath(path[2]).build().toUri();

                ServerHttpRequest newRequest = exchange.getRequest().mutate().uri(newUri)
                        .build();
                ServerWebExchange newExchange = exchange.mutate()
                        .request(newRequest)
                        .build();

                return chain.filter(newExchange);
            }

            // 서비스 타입에 따른 회원 기본 정보 조회
            String user_rep_id = request.getHeaders().get("user_rep_id").get(0);

            UserDTO userDTO = userService.getUserInfo(user_rep_id);
            System.out.println(userDTO);

            // 맨 앞 경로 분리
            String[] path = request.getURI().getPath().split("/");
            System.out.println(path[2]);
            System.out.println(path[1]);

            // 요청 api가 고객군, role 이 필요한지 확인
            ApiDTO apiDTO = apiService.getApi(request.getURI().getPath());
            System.out.println(apiDTO);

            List<CustDTO> custIdList = new ArrayList<>();
            if (apiDTO.getCustgp_yn().equals("Y")) {
                custIdList= userService.getCustIdList(user_rep_id);
            }
            System.out.println(custIdList);

            URI requestUri = exchange.getRequest().getURI();
            URI newUri = UriComponentsBuilder.fromUri(requestUri).host("127.0.0.1").port(8989)
                    .replacePath(path[2]).build().toUri();
            System.out.println(newUri);

            ServerHttpRequest newRequest = exchange.getRequest().mutate().uri(newUri)
                    .header("user_rep_id", user_rep_id)
                    .header("custIdList", custIdList.toString())
                    .build();


            ServerWebExchange newExchange = exchange.mutate()
                    .request(newRequest)
                    .build();


            // Custom Post Filter
            return chain.filter(newExchange)
                    .then(Mono.fromRunnable(() -> {
                        log.info("Custom POST filter : buffer code -> {}");

            }));
        };
    }

    public static class Config {
        // Put the configuration properties
    }
}

