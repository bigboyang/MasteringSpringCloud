package com.example.gatewayservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    Environment env;
    private final ObjectMapper objectMapper;

    public AuthorizationHeaderFilter(Environment env, ObjectMapper objectMapper) {
        super(Config.class);
        this.env = env;
        this.objectMapper = objectMapper;
        System.out.println("AuthorizationHeaderFilter.apply 생성됨");
    }

    public static class Config {

    }

    // login -> token -> users (with token) -> header(include token)
    @Override
    public GatewayFilter apply(Config config) {

        System.out.println("AuthorizationHeaderFilter.apply 실행");


        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("token filter request id -> {}", request.getId());

            // 필터 필요없는 url들 제외
            if (request.getURI().getPath().contains("/login")
                    || request.getURI().getPath().contains("/token")
                    || request.getURI().getPath().contains("/users")
            ){
                return chain.filter(exchange);
            }

            // 필터 적용 시작
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");

            if (!isJwtValid(jwt)){
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            // jwt에서 payload를 추출 후 기본정보 조회
            Claims claims = Jwts.parser()
                    .setSigningKey("secret")
                    .parseClaimsJws(jwt)
                    .getBody();

            String user_rep_id = claims.get("user_rep_id", String.class);
            String service_type = claims.get("service_type", String.class);

            // exchange 생성
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("user_rep_id", user_rep_id)
                    .header("service_type", service_type)
                    .build();

            log.info("AuthorizationHeaderFilter.apply modifiedRequest -> {}", modifiedRequest.getHeaders().get("user_rep_id"));




            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        });
    }

    // Mono, Flux -> Spring WebFlux
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(err);
        return response.setComplete();
    }

    /***
     * 토큰 유효성 검증
     */
    private boolean isJwtValid(String jwt) {
        // jwt token 유효성 검증
        boolean returnValue = true;

        String subject = null;
        try {
            subject = Jwts.parser().setSigningKey("secret")
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();
        }catch (Exception e) {
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()){
            returnValue = false;
        }

        return returnValue;
    }
}
