package com.example.preorder.common.utils;

import com.google.common.net.HttpHeaders;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Optional;

@Component
public final class HttpServletUtils {
    @Getter
    public enum CookieType {
        REFRESH_TOKEN("RefreshToken", (int) RedisKeyGenerator.RedisKeyType.REFRESH_TOKEN.getExpiration() / 1000);

        private final String key;
        private final int expiration;

        CookieType(String key, int expiration) {
            this.key = key;
            this.expiration = expiration;
        }
    }

    @Getter
    public enum HeaderType {
        ACCESS_TOKEN(HttpHeaders.AUTHORIZATION);

        private final String key;

        HeaderType(String key) {
            this.key = key;
        }
    }

    public Optional<String> getHeader(HeaderType type) {
        return Optional.ofNullable(getServletRequest().getHeader(type.getKey()));
    }

    public void putHeader(HeaderType type, String value) {
        getServletResponse().addHeader(type.getKey(), value);
    }

    public void addCookie(CookieType type, String value) {
        Cookie cookie = new Cookie(type.getKey(), value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(type.getExpiration());
        getServletResponse().addCookie(cookie);
    }

    public void deleteCookie(CookieType type) {
        getCookie(type).ifPresent((cookie) -> {
            cookie.setMaxAge(0);
            cookie.setValue("");
            getServletResponse().addCookie(cookie);
        });
    }

    public Optional<Cookie> getCookie(CookieType type) {
        return Arrays.stream(getServletRequest().getCookies())
                .filter(cookie -> cookie.getName().equals(type.getKey()))
                .findAny();
    }

    public HttpServletRequest getServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getServletResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }
}
