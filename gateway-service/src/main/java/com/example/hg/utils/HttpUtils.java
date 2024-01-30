package com.example.hg.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtils {

    public static Optional<String> getHeaderValue(HttpHeaders headers, String key) {
        List<String> values = headers.get(key);

        if (values == null) {
            return empty();
        }

        return ofNullable(values.get(0));
    }

    public static Optional<String> getCookieValue(MultiValueMap<?, ?> cookies, String key) {
        MultiValueMap<String, HttpCookie> httpCookies = (MultiValueMap<String, HttpCookie>) cookies;
        HttpCookie httpCookie = httpCookies.getFirst(key);

        if (httpCookie == null) {
            return empty();
        }

        return of(httpCookie.getValue());
    }
}
