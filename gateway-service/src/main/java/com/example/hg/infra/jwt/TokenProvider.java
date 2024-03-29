package com.example.hg.infra.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class TokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access_token_expiration}")
    private Long accessTokenExpiration;
    @Value("${jwt.refresh_token_expiration}")
    private Long refreshTokenExpiration;

    private AtomicLong atomicLong = new AtomicLong(1L);

    public String createAccessToken(String subject) {
        return createToken(subject, accessTokenExpiration);
    }

    public String createRefreshToken(String subject) {
        return createToken(subject, refreshTokenExpiration);
    }

    public String getSubject(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            log.debug("token is invalid : token : [{}]", token);
            return null;
        }
    }

    public boolean isValidateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.debug("token is invalid : token : [{}]", token);
            return false;
        }
    }

    private String createToken(String subject, Long expiration) {
        Assert.hasText(subject, "subject must be have text.");

        Claims claims = Jwts.claims().setSubject(subject);
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration + getRandomSeconds());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private long getRandomSeconds() {
        return (atomicLong.incrementAndGet() % 10) * 1000;
    }
}

