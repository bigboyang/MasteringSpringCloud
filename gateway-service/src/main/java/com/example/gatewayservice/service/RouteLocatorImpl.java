package com.example.gatewayservice.service;

import com.example.gatewayservice.dto.ApiDTO;
import com.example.gatewayservice.filter.AuthorizationHeaderFilter;
import com.example.gatewayservice.filter.CustomFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
public class RouteLocatorImpl  implements RouteLocator {

    private final RouteLocatorBuilder routeLocatorBuilder;

    // api 정보 가져오는 필터 추가
    private final ApiServiceImpl apiService;
    private final UserServiceImpl userService;

    // 토큰 필터 추가
    private final AuthorizationHeaderFilter tokenFilter;

    // 로그 필터 추가

    public RouteLocatorImpl(RouteLocatorBuilder routeLocatorBuilder, AuthorizationHeaderFilter tokenFilter,
                            ApiServiceImpl apiService,
                            UserServiceImpl userService
    ) {
        this.routeLocatorBuilder = routeLocatorBuilder;
        this.tokenFilter = tokenFilter;
        this.apiService = apiService;
        this.userService = userService;
    }

    @Override
    public Flux<Route> getRoutes() {

        log.info("RouteLocatorImpl.getRoutes()");

        RouteLocatorBuilder.Builder routesBuilder = routeLocatorBuilder.routes();

        // api 리스트 조회 후 flux에 담아서 필터 적용 후 리턴필요
        List<ApiDTO> apiList = apiService.getAllApis();

        for (ApiDTO serviceInfo : apiList) {
            log.info("{}", serviceInfo.getUrl());
        }

        return Flux.fromStream(apiList.stream()).map(
                apiRoute -> routesBuilder
                        .route(r -> r.path(apiRoute.getUrl())
                                .filters(f -> f.filter(tokenFilter.apply(new AuthorizationHeaderFilter.Config()))
                                        .filter(new CustomFilter(userService, apiService).apply(new CustomFilter.Config()))
//                                        .rewritePath("/user-service/(?<segment>.*)", "/${segment}")

                                )
                                .uri("http://127.0.0.1:8989")
                        )
        ).collectList().flatMapMany(builders -> routesBuilder.build().getRoutes());


        // login, 회원가입의 경우 토큰 필터 적용 안함
//        return routesBuilder
//                .route(r -> r.path("/user-service/**")
//                .filters(f -> f.filter(new CustomFilter().apply(new CustomFilter.Config()))
//                        .rewritePath("/user-service/(?<segment>.*)", "/${segment}")
//                        .filter(tokenFilter.apply(new AuthorizationHeaderFilter.Config()))
//                )
//                .uri("http://127.0.0.1:8989"))
//                .build().getRoutes();
    }
}
