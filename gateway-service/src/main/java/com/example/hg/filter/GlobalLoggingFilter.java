package com.example.hg.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import static com.example.hg.utils.HttpUtils.*;
import static org.springframework.core.Ordered.*;
import static org.springframework.http.HttpHeaders.*;

@Slf4j
@Component
public class GlobalLoggingFilter extends AbstractGatewayFilterFactory<GlobalLoggingFilter.Config> {

    public GlobalLoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter(((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            String identifier = request.getId();

            if (config.isPreLogger()) {
                printPreLog(identifier, request);
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    printPostLog(identifier, response);
                }
            }));
        }), HIGHEST_PRECEDENCE);
    }

    private void printPreLog(String identifier, ServerHttpRequest request) {
        String accessToken = getHeaderValue(request.getHeaders(), AUTHORIZATION).orElse(null);
        String refreshToken = getCookieValue(request.getCookies(), "RefreshToken").orElse(null);

        log.info("==========> ID: [{}] Method: [{}] URI: [{}]", identifier, request.getMethod(), request.getURI());
        log.info("==========> ID: [{}] AccessToken = [{}] RefreshToken: [{}]", identifier, accessToken, refreshToken);
    }

    private void printPostLog(String identifier, ServerHttpResponse response) {
        String accessToken = getHeaderValue(response.getHeaders(), AUTHORIZATION).orElse(null);
        String refreshToken = getCookieValue(response.getCookies(), "RefreshToken").orElse(null);

        log.info("<========== ID: [{}] Status Code: [{}]", identifier, response.getStatusCode());
        log.info("<========== ID: [{}] AccessToken: [{}] RefreshToken: [{}]", identifier, accessToken, refreshToken);
    }

    @Data
    static class Config {
        private boolean preLogger;
        private boolean postLogger;
    }
}
