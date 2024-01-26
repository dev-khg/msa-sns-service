package com.example.preorder.global.security.filter;

import com.example.preorder.common.exception.InternalErrorException;
import com.example.preorder.common.utils.HttpServletUtils;
import com.example.preorder.global.jwt.TokenProvider;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.example.preorder.common.utils.HttpServletUtils.CookieType.*;
import static com.example.preorder.common.utils.HttpServletUtils.HeaderType.*;
import static org.springframework.util.StringUtils.*;

@Component
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;
    private final HttpServletUtils httpServletUtils;

    public LoginFilter(AuthenticationManager authenticationManager,
                       ObjectMapper objectMapper,
                       TokenProvider tokenProvider,
                       HttpServletUtils httpServletUtils) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
        this.tokenProvider = tokenProvider;
        this.httpServletUtils = httpServletUtils;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            RequestLogin requestLogin = objectMapper.readValue(request.getInputStream(), RequestLogin.class);

            if(!hasText(requestLogin.getEmail()) || !hasText(requestLogin.getPassword())) {
                throw new BadCredentialsException("email and password must be not null.");
            }

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestLogin.getEmail(),
                            requestLogin.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserEntity credentials = (UserEntity) authResult.getCredentials();

        if (credentials == null) {
            throw new InternalErrorException("sorry! server is in error.");
        }

        String accessToken = tokenProvider.createAccessToken(credentials.getEmail());
        String refreshToken = tokenProvider.createRefreshToken(credentials.getEmail());

        httpServletUtils.addCookie(REFRESH_TOKEN, refreshToken);
        httpServletUtils.putHeader(ACCESS_TOKEN, accessToken);
    }

    @Getter
    static class RequestLogin {
        private String email;
        private String password;
    }
}
