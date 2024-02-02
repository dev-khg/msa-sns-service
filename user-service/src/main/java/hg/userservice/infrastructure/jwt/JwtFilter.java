package hg.userservice.infrastructure.jwt;

import hg.userservice.core.repository.KeyValueStorage;
import hg.userservice.core.vo.KeyType;
import hg.userservice.infrastructure.security.LoginUser;
import hg.userservice.infrastructure.security.UserIdDetailService;
import hg.userservice.utils.HttpServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

import static hg.userservice.core.vo.KeyType.*;
import static org.springframework.http.HttpHeaders.*;

//@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final KeyValueStorage keyValueStorage;
    private final UserIdDetailService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = HttpServletUtils.getHeaderValue(AUTHORIZATION).orElse(null);

        if (accessToken != null && !isBlackListToken(accessToken) && tokenProvider.isValidateToken(accessToken)) {
            String userId = tokenProvider.getSubject(accessToken);
            LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(userId);
            setAuthenticationContext(loginUser);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationContext(LoginUser loginUser) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, "", new ArrayList<>())
        );
    }

    private boolean isBlackListToken(String accessToken) {
        return keyValueStorage.getValue(BLACKLIST_TOKEN, accessToken).isPresent();
    }
}
