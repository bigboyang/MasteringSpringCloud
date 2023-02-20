package com.example.gatewayservice.config;

import com.example.gatewayservice.filter.AuthorizationHeaderFilter;
import com.example.gatewayservice.service.ApiServiceImpl;
import com.example.gatewayservice.service.InternalApiServiceImpl;
import com.example.gatewayservice.service.RouteLocatorImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;


import java.net.http.HttpHeaders;

@Slf4j
@Configuration
public class GatewayConfiguration implements WebFluxConfigurer {

    /**
    * cors 설정
    *  */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOriginPatterns("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                ;
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOriginPattern("*");
        ;
        UrlBasedCorsConfigurationSource corsConfigurationSource =
                new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(corsConfigurationSource);
    }

    /**
     * 토큰 필터 설정
     * */
    @Bean
    public AuthorizationHeaderFilter tokenFilter(Environment env, ObjectMapper objectMapper){
        return new AuthorizationHeaderFilter(env, objectMapper);
    }

    /**
     * 라우팅 설정
     * 토큰필터 등 필터들 라우터에 추가
     * */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, AuthorizationHeaderFilter tokenFilter, ApiServiceImpl apiService){
        return new RouteLocatorImpl(builder, tokenFilter, apiService);
    }


    /**
     * RefreshRoutesEvent 발생시키는 서비스
     * */
    @Bean
    public InternalApiServiceImpl internalApiService(
            ApplicationEventPublisher applicationEventPublisher) {
        return new InternalApiServiceImpl(applicationEventPublisher);
    }






}
