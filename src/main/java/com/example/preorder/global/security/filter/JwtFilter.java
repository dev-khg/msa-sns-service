package com.example.preorder.global.security.filter;

import com.example.preorder.common.utils.HttpServletUtils;
import com.example.preorder.common.utils.RedisKeyGenerator;
import com.example.preorder.global.jwt.TokenProvider;
import com.example.preorder.global.redis.RedisManager;
import com.example.preorder.global.security.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.preorder.common.utils.HttpServletUtils.HeaderType.*;
import static com.example.preorder.common.utils.RedisKeyGenerator.*;
import static com.example.preorder.common.utils.RedisKeyGenerator.RedisKeyType.*;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final RedisManager redisManager;
    private final HttpServletUtils httpServletUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getAccessToken(request);

        if (accessToken != null && !isBlackList(accessToken) && tokenProvider.isValidateToken(accessToken)) {
            String email = tokenProvider.getSubject(accessToken);
            LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(email);
            SecurityContextHolder.getContext().setAuthentication(loginUser);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isBlackList(String accessToken) {
        return redisManager.getValue(BLACKLIST_TOKEN, accessToken).isPresent();
    }

    private String getAccessToken(HttpServletRequest request) {
        String header = httpServletUtils.getHeader(ACCESS_TOKEN).orElse(null);

        if (header != null && header.startsWith("Bearer ")) {
            header = header.substring(7);
        }

        return header;
    }
}
