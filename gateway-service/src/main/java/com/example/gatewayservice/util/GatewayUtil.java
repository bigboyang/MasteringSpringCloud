package com.example.gatewayservice.util;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.net.InetSocketAddress;
import java.util.Optional;

@Slf4j
public class GatewayUtil {
    private GatewayUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getRemoteAddress(ServerHttpRequest request) {
        String ip = "";
        String[] headerNamesArr =
                new String[] {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
                        "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

        if (request == null) {
            return null;
        }

        try {
            for (int i = 0; i < headerNamesArr.length + 1; i++) {
                if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                    if (i < headerNamesArr.length) {
                        ip = request.getHeaders()
                                .getFirst(headerNamesArr[i]);
                    } else {
                        ip = Optional.ofNullable(request.getRemoteAddress())
                                .orElseGet(() -> new InetSocketAddress("0:0:0:0:0:0:0:1", 0))
                                .getHostName();
                    }

                }
            }

            String[] ipSet = Optional.ofNullable(ip)
                    .orElse("")
                    .split(",");
            if (ipSet.length > 1) {
                ip = ipSet[0].trim();
            }

            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                ip = "127.0.0.1";
            }

        } catch (Exception e) {
            log.warn("getRemoteAddress exception : ", e);
        }

        return ip;

    }


    public static String getRemoteAddress(ServerRequest request) {
        String ip = "";
        String[] headerNamesArr =
                new String[] {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
                        "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

        if (request == null) {
            return null;
        }

        try {
            for (int i = 0; i < headerNamesArr.length + 1; i++) {
                if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                    if (i < headerNamesArr.length) {
                        ip = request.headers()
                                .firstHeader(headerNamesArr[i]);
                    } else {
                        ip = request.remoteAddress()
                                .orElseGet(() -> new InetSocketAddress("0:0:0:0:0:0:0:1", 0))
                                .getHostName();
                    }

                }
            }

            String[] ipSet = Optional.ofNullable(ip)
                    .orElse("")
                    .split(",");
            if (ipSet.length > 1) {
                ip = ipSet[0].trim();
            }

            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                ip = "127.0.0.1";
            }

        } catch (Exception e) {
            log.warn("getRemoteAddress exception : ", e);
        }

        return ip;

    }

}
