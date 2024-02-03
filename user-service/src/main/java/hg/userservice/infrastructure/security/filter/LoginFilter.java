package hg.userservice.infrastructure.security.filter;

import com.example.commonproject.exception.BadRequestException;
import com.example.commonproject.exception.InternalServerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.KeyValueStorage;
import hg.userservice.infrastructure.jwt.TokenProvider;
import hg.userservice.presentation.request.LoginRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static hg.userservice.core.vo.KeyType.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.util.StringUtils.hasText;

@Component
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;
    private final KeyValueStorage keyValueStorage;

    public LoginFilter(AuthenticationManager authenticationManager,
                       ObjectMapper objectMapper,
                       TokenProvider tokenProvider,
                       KeyValueStorage keyValueStorage) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
        this.tokenProvider = tokenProvider;
        this.keyValueStorage = keyValueStorage;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            if (!hasText(loginRequest.getEmail()) || !hasText(loginRequest.getPassword())) {
                throw new BadRequestException("email and password must be not null.");
            }

            UsernamePasswordAuthenticationToken token = generateAuthenticationToken(loginRequest);

            return getAuthenticationManager().authenticate(token);
        } catch (IOException ioException) {
            throw new BadRequestException("Incorrect username or password.");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserEntity credentials = (UserEntity) authResult.getCredentials();

        if (credentials == null) {
            throw new InternalServerException("Sorry! Server has problem.");
        }

        String userId = String.valueOf(credentials.getId());

        String accessToken = tokenProvider.createAccessToken(userId);
        String refreshToken = tokenProvider.createRefreshToken(userId);

        keyValueStorage.putValue(REFRESH_TOKEN, userId, refreshToken);

        response.addHeader(AUTHORIZATION, accessToken);
        response.addCookie(createCookie(REFRESH_TOKEN.getKey(), refreshToken, (int) REFRESH_TOKEN.getExpiration()));
//        putHeader(AUTHORIZATION, accessToken);
//        createCookie(REFRESH_TOKEN.getKey(), refreshToken, (int) REFRESH_TOKEN.getExpiration());
    }

    private UsernamePasswordAuthenticationToken generateAuthenticationToken(LoginRequest loginRequest) {
        return new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()
        );
    }

    private Cookie createCookie(String key, String value, int expiration) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiration);
        return cookie;
    }
}
