package hg.userservice.presentation;

import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.vo.KeyType;
import hg.userservice.presentation.request.LoginRequest;
import hg.userservice.presentation.request.SignUpRequest;
import hg.userservice.testconfiguration.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static hg.userservice.core.vo.KeyType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class AccountControllerTest extends IntegrationTest {

    @Test
    @DisplayName("올바른 액세스 토큰으로 로그아웃 시, 블랙리스트 토큰에 저장되어야 한다.")
    void logout_request_valid_access_token() throws Exception {
        // given
        UserEntity userEntity = saveUserList.get(0);
        String accessToken = tokenProvider.createAccessToken(String.valueOf(userEntity.getId()));

        // when
        mockMvc.perform(delete("/sign-out")
                .header(AUTHORIZATION, accessToken)
        ).andExpect(status().isNoContent());

        // then
        assertTrue(keyValueStorage.getValue(BLACKLIST_TOKEN, accessToken).isPresent());
    }

    @Test
    @DisplayName("올바른 리프레쉬 토큰으로 로그아웃 시, 해당 토큰 정보가 저장소에서 사라지고, 쿠키값을 무효화해야 한다.")
    void logout_request_valid_refresh_token() throws Exception {
        // given
        UserEntity userEntity = saveUserList.get(0);
        String refreshToken = tokenProvider.createRefreshToken(String.valueOf(userEntity.getId()));
        Cookie refreshTokenCookie = createMockCookie(refreshToken, REFRESH_TOKEN);
        keyValueStorage.putValue(REFRESH_TOKEN, String.valueOf(userEntity.getId()), refreshToken);

        // when
        MvcResult mvcResult = mockMvc.perform(delete("/sign-out")
                        .header(AUTHORIZATION, refreshTokenCookie)
                        .cookie(refreshTokenCookie)
                ).andExpect(status().isNoContent())
                .andReturn();

        // then
        assertTrue(keyValueStorage.getValue(REFRESH_TOKEN, refreshToken).isEmpty());
        Cookie cookie = mvcResult.getResponse().getCookie(REFRESH_TOKEN.getKey());
        assertTrue(cookie.getMaxAge() < 1);
        assertEquals(cookie.getValue(), "");
    }

    @Test
    @DisplayName("올바르지 않은 리프레쉬 토큰으로 재발급 요청 시, 새로운 리프레시 토큰 쿠키나 액세스 토큰 헤더가 반환되면 안된다.")
    void reissue_token_with_invalid_refresh_token() throws Exception {
        // given
        UserEntity userEntity = saveUserList.get(0);
        String refreshToken = tokenProvider.createRefreshToken(String.valueOf(userEntity.getId()));
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN.getKey(), refreshToken + "a");

        // when
        MvcResult mvcResult = mockMvc.perform(post("/reissue")
                        .cookie(refreshTokenCookie)
                ).andExpect(status().isNoContent())
                .andReturn();

        // then
        Cookie cookie = mvcResult.getResponse().getCookie(REFRESH_TOKEN.getKey());
        assertNull(cookie);
        assertNull(mvcResult.getResponse().getHeader(AUTHORIZATION));
    }

    @Test
    @DisplayName("올바른 리프레쉬 토큰으로 재발급 요청 시, 새로운 리프레시 토큰 쿠키와 액세스 토큰 헤더가 반환되어야 한다.")
    void reissue_token_with_valid_refresh_token() throws Exception {
        // given
        UserEntity userEntity = saveUserList.get(0);
        String refreshToken = tokenProvider.createRefreshToken(String.valueOf(userEntity.getId()));
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN.getKey(), refreshToken);
        keyValueStorage.putValue(REFRESH_TOKEN, String.valueOf(userEntity.getId()), refreshToken);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/reissue")
                        .cookie(refreshTokenCookie)
                ).andExpect(status().isNoContent())
                .andReturn();

        // then
        Cookie cookie = mvcResult.getResponse().getCookie(REFRESH_TOKEN.getKey());
        assertNotNull(mvcResult.getResponse().getHeader(AUTHORIZATION));
        assertNotNull(cookie);
    }

    @Test
    @DisplayName("존재하지 않는 유저로 로그인 요청 시, UnAuthorized가 응답되어야 한다.")
    void login_with_invalid_user_email() throws Exception {
        // given
        LoginRequest requestBody = new LoginRequest(createRandomUUID(), createRandomUUID());

        // when
        mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(requestBody))
        ).andExpect(status().isUnauthorized());

        // then
    }

    @Test
    @DisplayName("올바르지 않은 비밀번호로 로그인 요청 시, UnAuthorized가 응답되어야 한다.")
    void login_with_invalid_user_password() throws Exception {
        // given
        String password = createRandomUUID();
        UserEntity userEntity = UserEntity.create(createRandomUUID(), createRandomUUID(), passwordEncoder.encode(password));
        userRepository.save(userEntity);
        LoginRequest requestBody = new LoginRequest(userEntity.getEmail(), createRandomUUID());

        // when
        mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(requestBody))
        ).andExpect(status().isUnauthorized());

        // then
    }

    @Test
    @DisplayName("올바른 아이디 비밀번호로 로그인 요청 시, 리프레쉬 토큰 쿠키와 액세스 토큰 헤더를 반환한다.")
    void login_with_valid_user_email_and_password() throws Exception {
        // given
        String password = createRandomUUID();
        UserEntity userEntity = UserEntity.create(createRandomUUID(), createRandomUUID(), passwordEncoder.encode(password));
        userRepository.save(userEntity);
        LoginRequest requestBody = new LoginRequest(userEntity.getEmail(), password);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(requestBody))
                ).andExpect(status().isOk())
                .andReturn();

        // then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertNotNull(response.getHeader(AUTHORIZATION));
        Cookie cookie = response.getCookie(REFRESH_TOKEN.getKey());
        assertNotNull(cookie);
        assertNotNull(keyValueStorage.getValue(REFRESH_TOKEN, cookie.getValue()));
    }

    @Test
    @DisplayName("정상 회원가입 시, 액세스 토큰과 리프레쉬 토큰이 반환되어야 한다.")
    void sign_up_with_duplicated_username_or_email() throws Exception {
        // given
        SignUpRequest signUpRequest = new SignUpRequest(
                "test@test.com",
                "ABCDEFGHIJKL",
                "ABCDEFGHIJKL",
                "123456");
        keyValueStorage.putValue(EMAIL_CERT, signUpRequest.getEmail(), signUpRequest.getAuthCode());

        // when
        MvcResult mvcResult = mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest))
                ).andExpect(status().isNoContent())
                .andReturn();

        // then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertNotNull(response.getHeader(AUTHORIZATION));
        Cookie cookie = response.getCookie(REFRESH_TOKEN.getKey());
        assertNotNull(cookie);
        assertNotNull(keyValueStorage.getValue(REFRESH_TOKEN, cookie.getValue()));
    }

    private Cookie createMockCookie(String refreshToken, KeyType type) {
        Cookie cookie = new Cookie(type.getKey(), refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge((int) type.getExpiration());
        cookie.setHttpOnly(true);

        return cookie;
    }

}