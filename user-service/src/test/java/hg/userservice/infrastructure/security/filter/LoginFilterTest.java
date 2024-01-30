package hg.userservice.infrastructure.security.filter;

import com.example.commonproject.exception.BadRequestException;
import com.example.commonproject.exception.InternalServerException;
import com.example.commonproject.exception.UnAuthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.infrastructure.jwt.TokenProvider;
import hg.userservice.infrastructure.redis.RedisManager;
import hg.userservice.presentation.request.LoginRequest;
import hg.userservice.presentation.request.SignUpRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class LoginFilterTest {
    @InjectMocks
    LoginFilter loginFilter;

    @Mock
    ObjectMapper objectMapper;
    @Mock
    RedisManager redisManager;
    @Mock
    TokenProvider tokenProvider;
    @Mock
    AuthenticationManager authenticationManager;

    @Test
    @DisplayName("공백의 이메일로 로그인 요청시 예외가 발생해야 한다.")
    void invalid_email_request_attempt_throws_exception() throws Exception {
        // given
        LoginRequest nullEmail = mock(LoginRequest.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // when
        when(objectMapper.readValue((InputStream) any(), eq(LoginRequest.class)))
                .thenReturn(nullEmail);
        when(nullEmail.getEmail()).thenReturn(" ");
        when(nullEmail.getPassword()).thenReturn(createRandomUUID());

        // then
        assertThatThrownBy(
                () -> loginFilter.attemptAuthentication(mockRequest, mockResponse)
        ).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("공백의 비밀번호로 로그인 요청시 예외가 발생해야 한다.")
    void invalid_password_request_attempt_throws_exception() throws Exception {
        // given
        LoginRequest nullPassword = mock(LoginRequest.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // when
        when(objectMapper.readValue((InputStream) any(), eq(LoginRequest.class)))
                .thenReturn(nullPassword);
        when(nullPassword.getEmail()).thenReturn(createRandomUUID());
        when(nullPassword.getPassword()).thenReturn(" ");

        // then
        assertThatThrownBy(
                () -> loginFilter.attemptAuthentication(mockRequest, mockResponse)
        ).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("파싱 에러가 나면, 예외가 발생해야 한다.")
    void invalid_request_body_parsing_error() throws Exception {
        // given
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // when
        when(objectMapper.readValue((InputStream) any(), eq(LoginRequest.class)))
                .thenThrow(IOException.class);

        // then
        assertThatThrownBy(
                () -> loginFilter.attemptAuthentication(mockRequest, mockResponse)
        ).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("공백이 아닌 아이디 비밀번호로 로그인 요청시 임시 권한이 설정되어야 한다.")
    void valid_input_request() throws Exception {
        // given
        LoginRequest requestLogin = mock(LoginRequest.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // when
        when(objectMapper.readValue((InputStream) any(), eq(LoginRequest.class)))
                .thenReturn(requestLogin);
        when(requestLogin.getEmail()).thenReturn(createRandomUUID());
        when(requestLogin.getPassword()).thenReturn(createRandomUUID());

        // then
        loginFilter.attemptAuthentication(mockRequest, mockResponse);

        verify(authenticationManager, times(1))
                .authenticate(any());
    }

    @Test
    @DisplayName("로그인 권한 설정 후, Credentials가 null 반환 시 예외가 반환된다.")
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
        ).isInstanceOf(InternalServerException.class);
    }

    @Test
    @DisplayName("올바른 인증 성공시 올바른 토큰이 반환되어야 한다.")
    void valid_success_authentication() throws ServletException, IOException {
        // given
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest, mockResponse));
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

        verify(tokenProvider, times(1)).createAccessToken(any());
        verify(tokenProvider, times(1)).createRefreshToken(any());
        verify(mockResponse, times(1)).addCookie(any());
        verify(mockResponse, times(1)).addHeader(any(), any());
        verify(redisManager, times(1)).putValue(any(), any(), any());
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}