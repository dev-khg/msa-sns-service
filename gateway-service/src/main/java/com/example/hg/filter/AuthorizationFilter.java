package com.example.hg.filter;

import com.example.hg.jwt.TokenProvider;
import com.example.hg.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.apache.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {
    private final TokenProvider tokenProvider;

    public AuthorizationFilter(TokenProvider tokenProvider) {
        super(Config.class);
        this.tokenProvider = tokenProvider;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            Optional<String> accessToken = HttpUtils.getHeaderValue(request.getHeaders(), AUTHORIZATION);
            Optional<String> refreshToken = HttpUtils.getCookieValue(request.getCookies(), "RefreshToken");

            if (accessToken.isEmpty() && refreshToken.isEmpty()) {
                return onError(exchange);
            }

            if(accessToken.isEmpty() && !reissueRefreshToken()) {
                return onError(exchange);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(UNAUTHORIZED);

        return response.setComplete();
    }

    private boolean reissueRefreshToken() {
        return false;
    }

    static class Config {

    }
}
