package hg.userservice.core.service;

import hg.userservice.core.repository.KeyValueStorage;
import hg.userservice.core.vo.KeyType;
import hg.userservice.infrastructure.jwt.TokenProvider;
import hg.userservice.testconfiguration.EmbeddedRedisConfiguration;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {
    @InjectMocks
    AuthService authService;

    @Mock
    KeyValueStorage keyValueStorage;
    @Mock
    TokenProvider tokenProvider;

    @Test
    @DisplayName("로그아웃 시, 헤더와 쿠키가 없으면 토큰 판단 로직이 호출되면 안된다.")
    void success_logout_no_header_and_no_cookie() {
        // given
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        // when
        when(mockRequest.getHeader(AUTHORIZATION))
                .thenReturn(null);
        when(mockRequest.getCookies())
                .thenReturn(null);

        // then
        authService.logout();

        verify(tokenProvider, times(0))
                .isValidateToken(any());
    }

    @Test
    @DisplayName("로그아웃 시, 헤더 값이 있으면 블랙리스트 토큰에 저장되어야 한다.")
    void success_logout_has_header() {
        // given
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
        String accessToken = createRandomUUID();

        // when
        when(mockRequest.getHeader(AUTHORIZATION))
                .thenReturn(accessToken);
        when(tokenProvider.isValidateToken(accessToken))
                .thenReturn(true);

        // then
        authService.logout();

        verify(tokenProvider, times(1))
                .isValidateToken(accessToken);
        verify(keyValueStorage, times(1))
                .putValue(any(KeyType.class), any(), any());
    }

    @Test
    @DisplayName("로그아웃 시, 쿠키 값이 있으면 저장소에서 삭제되어야 한다.")
    void success_logout_has_cookie() {
        // given
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
        String refreshToken = createRandomUUID();
        Cookie[] cookies = {
                new Cookie(KeyType.REFRESH_TOKEN.getKey(), refreshToken)
        };

        // when
        when(mockRequest.getCookies())
                .thenReturn(cookies);
        when(tokenProvider.isValidateToken(refreshToken))
                .thenReturn(true);

        // then
        authService.logout();

        verify(tokenProvider, times(1))
                .isValidateToken(refreshToken);
        verify(keyValueStorage, times(1))
                .deleteKey(any(KeyType.class), any());
    }

    @Test
    @DisplayName("리프레쉬 토큰이 유효하지 않으면, 토큰 발급 관련 로직이 호출되면 안된다.")
    void failure_reissue_token_without_refresh_token() {
        // given
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
        String refreshToken = createRandomUUID();
        Cookie[] cookies = {
                new Cookie(KeyType.REFRESH_TOKEN.getKey(), refreshToken)
        };

        // when
        when(mockRequest.getCookies())
                .thenReturn(cookies);
        when(tokenProvider.isValidateToken(refreshToken))
                .thenReturn(false);

        authService.reissueToken();
        // then
        verify(tokenProvider, times(0))
                .getSubject(any());
    }

    @Test
    @DisplayName("리프레쉬 토큰이 유효하면, 토큰 발급 관련 로직이 호출되어야한다.")
    void success_reissue_token_with_refresh_token() {
        // given
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest, mockResponse));
        String refreshToken = createRandomUUID();
        Cookie[] cookies = {
                new Cookie(KeyType.REFRESH_TOKEN.getKey(), refreshToken)
        };

        // when
        when(mockRequest.getCookies())
                .thenReturn(cookies);
        when(tokenProvider.isValidateToken(refreshToken))
                .thenReturn(true);
        when(tokenProvider.getSubject(refreshToken))
                .thenReturn(String.valueOf(1L));

        authService.reissueToken();

        // then
        verify(tokenProvider, times(1))
                .getSubject(any());
        verify(keyValueStorage, times(1))
                .deleteKey(any(), any());
        verify(keyValueStorage, times(1))
                .putValue(any(KeyType.class), any(), any());
        verify(mockResponse, times(1))
                .addHeader(any(), any());
        verify(mockResponse, times(1))
                .addCookie(any());
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}