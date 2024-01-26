package com.example.preorder.global.security.filter;

import com.example.preorder.common.utils.HttpServletUtils;
import com.example.preorder.global.jwt.TokenProvider;
import com.example.preorder.global.redis.RedisManager;
import com.example.preorder.global.security.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtFilterTest {
    @InjectMocks
    JwtFilter jwtFilter;

    @Mock
    TokenProvider tokenProvider;
    @Mock
    RedisManager redisManager;
    @Mock
    HttpServletUtils httpServletUtils;
    @Mock
    UserDetailsService userDetailsService;

    @BeforeEach
    void beforeEach() {

    }

    @Test
    @DisplayName("토큰이 없으면, 인증 권한을 가지면 안된다.")
    void invalid_request_without_token() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // when
        when(httpServletUtils.getHeader(any()))
                .thenReturn(empty());

        // then
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(userDetailsService, times(0))
                .loadUserByUsername(any());
    }

    @Test
    @DisplayName("블랙리스트 토큰으로 요청시, 인증 권한이 설정되면 안된다.")
    void invalid_request_with_black_list_token() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // when
        when(httpServletUtils.getHeader(any()))
                .thenReturn(ofNullable("Bearer temp"));
        when(redisManager.getValue(any(), any()))
                .thenReturn(ofNullable("temp"));

        // then
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(userDetailsService, times(0))
                .loadUserByUsername(any());
    }

    @Test
    @DisplayName("유효한 형식이 아닌 토큰으로 요청시, 인증 권한이 설정되면 안된다.")
    void invalid_request_with_invalid_token() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // when
        when(httpServletUtils.getHeader(any()))
                .thenReturn(ofNullable("Bearer te313123mp"));
        when(userDetailsService.loadUserByUsername(any()))
                .thenReturn(null);
        when(redisManager.getValue(any(), any()))
                .thenReturn(ofNullable("temp"));
        when(tokenProvider.isValidateToken(any()))
                .thenReturn(false);

        // then
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(userDetailsService, times(0))
                .loadUserByUsername(any());
    }

    @Test
    @DisplayName("올바른 토큰으로 요청시, 인증 권한이 설정되어야 한다.")
    void valid_request_auth() throws ServletException, IOException {
        // given
        LoginUser mock = mock(LoginUser.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // when
        when(httpServletUtils.getHeader(any()))
                .thenReturn(ofNullable("Bearer temp"));
        when(redisManager.getValue(any(), any()))
                .thenReturn(empty());
        when(tokenProvider.isValidateToken(any()))
                .thenReturn(true);
        when(userDetailsService.loadUserByUsername(any()))
                .thenReturn(mock);

        // then
        jwtFilter.doFilterInternal(request, response, filterChain);
        assertEquals(
                SecurityContextHolder.getContext().getAuthentication(),
                mock
        );
    }
}