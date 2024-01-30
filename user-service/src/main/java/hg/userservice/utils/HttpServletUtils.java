package hg.userservice.utils;

import hg.userservice.core.vo.KeyType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpServletUtils {

    public static Optional<String> getHeaderValue(String key) {
        return ofNullable(getServletRequest().getHeader(key));
    }

    public static void putHeader(String key, String value) {
        getServletResponse().addHeader(key, value);
    }

    public static void addCookie(String key, String value, int expiration) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiration);
        getServletResponse().addCookie(cookie);
    }

    public static Optional<Cookie> getCookie(String key) {
        return Arrays.stream(getServletRequest().getCookies())
                .filter(cookie -> cookie.getName().equals(key))
                .findAny();
    }

    public static HttpServletRequest getServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getServletResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }
}
