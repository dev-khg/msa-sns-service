package com.example.hg.filter;

import com.example.hg.infra.redis.KeyType;
import com.example.hg.infra.redis.RedisManager;
import com.example.hg.infra.jwt.TokenProvider;
import com.example.hg.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
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
    private final RedisManager redisManager;

    public AuthorizationFilter(TokenProvider tokenProvider, RedisManager redisManager) {
        super(Config.class);
        this.tokenProvider = tokenProvider;
        this.redisManager = redisManager;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            Optional<String> accessToken = HttpUtils.getHeaderValue(request.getHeaders(), AUTHORIZATION);

            if (accessToken.isEmpty()) {
                return onError(exchange);
            } else if (!tokenProvider.isValidateToken(accessToken.get()) || isBlackListToken(accessToken.get())) {
                return onError(exchange);
            }

            String userId = tokenProvider.getSubject(accessToken.get());

            ServerHttpRequest build = request.mutate().header("user-id", userId).build();
            ServerWebExchange newWebExchange = exchange.mutate().request(build).build();

            return chain.filter(newWebExchange);
        };
    }

    private boolean isBlackListToken(String accessToken) {
        return redisManager.getValue(KeyType.BLACKLIST_TOKEN, accessToken).isPresent();
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(UNAUTHORIZED);

        return response.setComplete();
    }

    static class Config {

    }
}
