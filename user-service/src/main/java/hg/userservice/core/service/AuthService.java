package hg.userservice.core.service;

import hg.userservice.core.repository.KeyValueStorage;
import hg.userservice.core.vo.KeyType;
import hg.userservice.infrastructure.jwt.TokenProvider;
import hg.userservice.utils.HttpServletUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static hg.userservice.core.vo.KeyType.*;
import static hg.userservice.utils.HttpServletUtils.*;
import static org.apache.http.HttpHeaders.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final KeyValueStorage keyValueStorage;
    private final TokenProvider tokenProvider;

    /**
     * Access Token, Refresh Token 전부 삭제.
     */
    public void logout() {
        HttpServletUtils.getHeaderValue(AUTHORIZATION).ifPresent(accessToken -> {
            if (tokenProvider.isValidateToken(accessToken)) {
                keyValueStorage.putValue(BLACKLIST_TOKEN, accessToken, "");
            }
        });

        HttpServletUtils.getCookie(REFRESH_TOKEN.getKey()).ifPresent(cookie -> {
            String refreshToken = cookie.getValue();
            if (tokenProvider.isValidateToken(refreshToken)) {
                String userId = tokenProvider.getSubject(refreshToken);
                keyValueStorage.deleteKey(REFRESH_TOKEN, userId);
                deleteCookie(REFRESH_TOKEN.getKey());
            }
        });
    }

    /**
     * RefreshToken 확인 후, Access 토큰, Refresh 토큰 발급
     */
    public void reissueToken() {
        HttpServletUtils.getCookie(REFRESH_TOKEN.getKey()).ifPresent(cookie -> {
            String refreshToken = cookie.getValue();

            if (tokenProvider.isValidateToken(refreshToken) && isStoredRefreshToken(refreshToken)) {
                String userId = tokenProvider.getSubject(refreshToken);
                keyValueStorage.deleteKey(REFRESH_TOKEN, userId);

                issueToken(Long.parseLong(userId));
            }
        });
    }

    /**
     * @param userId : Token Subject
     */
    public void issueToken(Long userId) {
        String subject = String.valueOf(userId);

        String accessToken = tokenProvider.createAccessToken(subject);
        String refreshToken = tokenProvider.createRefreshToken(subject);

        keyValueStorage.putValue(REFRESH_TOKEN, subject, refreshToken);

        putHeader(AUTHORIZATION, accessToken);
        addCookie(REFRESH_TOKEN.getKey(), refreshToken, (int) REFRESH_TOKEN.getExpiration());
    }

    private boolean isStoredRefreshToken(String refreshToken) {
        String userId = tokenProvider.getSubject(refreshToken);
        Optional<String> value = keyValueStorage.getValue(REFRESH_TOKEN, userId);
        return value.isPresent() && value.get().equals(refreshToken);
    }
}
