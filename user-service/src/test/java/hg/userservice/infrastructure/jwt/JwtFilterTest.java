package hg.userservice.infrastructure.jwt;

import hg.userservice.infrastructure.redis.RedisManager;
import hg.userservice.infrastructure.security.LoginUser;
import hg.userservice.utils.HttpServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.*;

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
    UserDetailsService userDetailsService;

    @Test
    @DisplayName("토큰이 없으면, 인증 권한을 가지면 안된다.")
    void invalid_request_without_token() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        FilterChain filterChain = mock(FilterChain.class);

        // when
        when(request.getHeader(AUTHORIZATION))
                .thenReturn(null);

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
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        FilterChain filterChain = mock(FilterChain.class);
        String accessToken = createRandomUUID();

        // when
        when(request.getHeader(AUTHORIZATION))
                .thenReturn(accessToken);
        when(redisManager.getValue(any(), any()))
                .thenReturn(ofNullable("temp"));

        // then
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(userDetailsService, times(0))
                .loadUserByUsername(any());
    }

    @Test
    @DisplayName("올바른 토큰으로 요청시, 인증 권한이 설정되어야 한다.")
    void valid_request_auth() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        FilterChain filterChain = mock(FilterChain.class);
        LoginUser mock = mock(LoginUser.class);
        String accessToken = createRandomUUID();

        // when
        when(request.getHeader(AUTHORIZATION))
                .thenReturn(accessToken);
        when(redisManager.getValue(any(), any()))
                .thenReturn(empty());
        when(tokenProvider.isValidateToken(any()))
                .thenReturn(true);
        when(userDetailsService.loadUserByUsername(any()))
                .thenReturn(mock);

        // then
        jwtFilter.doFilterInternal(request, response, filterChain);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}