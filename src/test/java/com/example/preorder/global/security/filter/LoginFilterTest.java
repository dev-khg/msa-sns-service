package com.example.preorder.global.security.filter;

import com.example.preorder.common.exception.InternalErrorException;
import com.example.preorder.common.utils.HttpServletUtils;
import com.example.preorder.global.jwt.TokenProvider;
import com.example.preorder.user.core.entity.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.example.preorder.global.security.filter.LoginFilter.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class LoginFilterTest {
    @InjectMocks
    LoginFilter loginFilter;

    @Mock
    ObjectMapper objectMapper;
    @Mock
    TokenProvider tokenProvider;
    @Mock
    HttpServletUtils httpServletUtils;
    @Mock
    AuthenticationManager authenticationManager;

    @Test
    @DisplayName("공백의 이메일로 요청시 예외가 발생해야 한다.")
    void invalid_email_request_attempt_throws_exception() throws Exception {
        // given
        RequestLogin nullEmail = mock(RequestLogin.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // when
        when(objectMapper.readValue((InputStream) any(), eq(RequestLogin.class)))
                .thenReturn(nullEmail);
        when(nullEmail.getEmail()).thenReturn(" ");
        when(nullEmail.getPassword()).thenReturn(createRandomUUID());

        // then
        assertThatThrownBy(
                () -> loginFilter.attemptAuthentication(mockRequest, mockResponse)
        ).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("공백의 비밀번호로 요청시 예외가 발생해야 한다.")
    void invalid_password_request_attempt_throws_exception() throws Exception {
        // given
        RequestLogin nullPassword = mock(RequestLogin.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // when
        when(objectMapper.readValue((InputStream) any(), eq(RequestLogin.class)))
                .thenReturn(nullPassword);
        when(nullPassword.getEmail()).thenReturn(createRandomUUID());
        when(nullPassword.getPassword()).thenReturn(" ");

        // then
        assertThatThrownBy(
                () -> loginFilter.attemptAuthentication(mockRequest, mockResponse)
        ).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("공백이 아닌 아이디 비밀번호로 요청시 임시 권한이 설정되어야 한다.")
    void valid_input_request() throws Exception {
        // given
        RequestLogin requestLogin = mock(RequestLogin.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // when
        when(objectMapper.readValue((InputStream) any(), eq(RequestLogin.class)))
                .thenReturn(requestLogin);
        when(requestLogin.getEmail()).thenReturn(createRandomUUID());
        when(requestLogin.getPassword()).thenReturn(createRandomUUID());

        // then
        loginFilter.attemptAuthentication(mockRequest, mockResponse);

        verify(authenticationManager, times(1))
                .authenticate(any());
    }

    @Test
    @DisplayName("올바르지 않은 인증 성공시 반환 시 예외가 반환된다.")
    void invalid_success_authentication() {
        // given
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        FilterChain mockFilterChain = mock(FilterChain.class);
        Authentication mockAuthentication = mock(Authentication.class);

        // when
        when(mockAuthentication.getCredentials())
                .thenReturn(null);

        // then
        assertThatThrownBy(
                () -> loginFilter.successfulAuthentication(
                        mockRequest, mockResponse, mockFilterChain, mockAuthentication
                )
        ).isInstanceOf(InternalErrorException.class);
    }

    @Test
    @DisplayName("올바른 인증 성공시 올바른 토큰이 반환되어야 한다.")
    void valid_success_authentication() throws ServletException, IOException {
        // given
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        FilterChain mockFilterChain = mock(FilterChain.class);
        Authentication mockAuthentication = mock(Authentication.class);
        UserEntity mockUserEntity = mock(UserEntity.class);

        // when
        when(mockAuthentication.getCredentials())
                .thenReturn(mockUserEntity);

        // then
        loginFilter.successfulAuthentication(
                mockRequest, mockResponse, mockFilterChain, mockAuthentication
        );

        verify(tokenProvider, times(1))
                .createAccessToken(any());
        verify(tokenProvider, times(1))
                .createRefreshToken(any());
        verify(httpServletUtils, times(1))
                .addCookie(any(), any());
        verify(httpServletUtils, times(1))
                .putHeader(any(), any());
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}