package com.example.preorder.user.presentation;

import com.example.preorder.common.ApiResponse;
import com.example.preorder.common.utils.RandomGenerator;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.presentation.resources.request.UserChangePasswordRequest;
import com.example.preorder.user.presentation.resources.request.UserInfoEditRequest;
import com.example.preorder.user.presentation.resources.request.UserSignUpRequest;
import com.example.preorder.user.presentation.resources.response.UserInfoResponse;
import com.example.preorder.utils.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static com.example.preorder.common.utils.RedisKeyGenerator.RedisKeyType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class UserControllerTest extends IntegrationTest {

    @DisplayName("회원가입시 이메일, 이름, 비밀번호, 인증코드가 비어있으면 예외반환")
    @ParameterizedTest
    @MethodSource("generateNullFieldSignUpData")
    void invalid_null_filed(String email, String name, String password, String authCode) throws Exception {
        // given
        UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                email,
                name,
                password,
                createRandomUUID(),
                authCode
        );
        MockMultipartFile jsonMultiPartFile = objectToMultiPart("user_sign_up", userSignUpRequest);

        // when
        mockMvc.perform(multipart(HttpMethod.POST, "/user")
                .file(jsonMultiPartFile)
                .file(jsonMultiPartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("동일한 이메일로 회원가입시 예외반환")
    void invalid_duplicate_email_filed() throws Exception {
        // given
        UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                userEntity.getEmail(),
                createRandomUUID(),
                password,
                createRandomUUID(),
                "123456"
        );
        MockMultipartFile jsonMultiPartFile = objectToMultiPart("user_sign_up", userSignUpRequest);

        // when
        mockMvc.perform(multipart("/user")
                .file(jsonMultiPartFile)
                .file(jsonMultiPartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("동일한 아이디로 회원가입시 예외반환")
    void invalid_duplicate_name_filed() throws Exception {
        // given
        UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                "test@test.com",
                userEntity.getUsername(),
                password,
                createRandomUUID(),
                "123456"
        );
        MockMultipartFile jsonMultiPartFile = objectToMultiPart("user_sign_up", userSignUpRequest);

        // when
        mockMvc.perform(multipart("/user")
                .file(jsonMultiPartFile)
                .file(jsonMultiPartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상 회원가입")
    void valid_sign_up() throws Exception {
        // given
        UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                "TEST@test.com",
                createRandomUUID().substring(10),
                password,
                createRandomUUID(),
                String.valueOf(RandomGenerator.generateSixDigitsRandomCode())
        );
        redisManager.putValue(EMAIL_CERT, userSignUpRequest.getEmail(), userSignUpRequest.getAuthCode());
        MockMultipartFile jsonMultiPartFile = objectToMultiPart("user_sign_up", userSignUpRequest);

        // when
        MvcResult mvcResult = mockMvc.perform(
                multipart("/user")
                        .file(jsonMultiPartFile)
                        .file(jsonMultiPartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andReturn();

        // then
        checkHasHeader(mvcResult, AUTHORIZATION);
        checkHasCookie(mvcResult, "RefreshToken");
    }

    @Test
    @DisplayName("비정상 토큰으로 토큰 재발급시 null 반환")
    void invalid_reissue_toke() throws Exception {
        // given

        // when
        MvcResult mvcResult = mockMvc.perform(
                post("/user/token")
                        .cookie(new Cookie("RefreshToken", refreshToken + "a"))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andReturn();

        // then
        checkHeaderNull(mvcResult, AUTHORIZATION);
        checkCookieNull(mvcResult, "RefreshToken");
    }

    @Test
    @DisplayName("정상 토큰으로 토큰 재발급시 새로운 토큰 반환")
    void valid_reissue_toke() throws Exception {
        // given

        // when
        MvcResult mvcResult = mockMvc.perform(
                post("/user/token")
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                        .cookie(new Cookie("RefreshToken", refreshToken))
        ).andReturn();

        // then
        assertNotNull(mvcResult.getResponse().getHeader(AUTHORIZATION));
        assertNotEquals(mvcResult.getResponse().getCookie("RefreshToken").getValue(), refreshToken);
    }

    @Test
    @DisplayName("비정상 토큰으로 비밀번호 변경시 예외 반환")
    void invalid_token_edit_password() throws Exception {
        // given
        String currentPassword = password;
        String newPassword = "1234567890";
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(currentPassword, newPassword);

        // when
        mockMvc.perform(
                patch("/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangePasswordRequest))
                        .header(AUTHORIZATION, "Bearer " + accessToken + "a")
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("현재 비밀번호가 일치하지 않을 시 예외 반환")
    void invalid_current_password_edit_password() throws Exception {
        // given
        String currentPassword = password + "a";
        String newPassword = "1234567890";
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(currentPassword, newPassword);

        // when
        mockMvc.perform(
                patch("/user/password")
                        .content(objectMapper.writeValueAsString(userChangePasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + accessToken)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("현재 비밀번호가 일치하면 비밀번호가 변경되어야 한다.")
    void valid_current_password_edit_password() throws Exception {
        // given
        String currentPassword = password;
        String newPassword = "1234567890";
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(currentPassword, newPassword);

        // when
        mockMvc.perform(
                patch("/user/password")
                        .content(objectMapper.writeValueAsString(userChangePasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + accessToken)
        ).andExpect(status().isNoContent());

        // then
        UserEntity changedEntity = userRepository.findById(userEntity.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, changedEntity.getPassword()));
    }

    @Test
    @DisplayName("존재하지 않는 유저 정보 요청시 정상응답해야한다.")
    void invalid_user_info_return_valid() throws Exception {
        // given
        Long existsId = Long.MAX_VALUE;

        // when
        mockMvc.perform(
                get("/user/info/" + existsId)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하는 유저 정보 요청시 정상응답해야한다.")
    void valid_user_info_return_valid() throws Exception {
        // given
        Long existsId = userEntity.getId();

        // when
        MvcResult mvcResult = mockMvc.perform(
                        get("/user/info/" + existsId)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        // then
        ApiResponse<UserInfoResponse> apiResponse = readJson(
                mvcResult.getResponse().getContentAsString(), UserInfoResponse.class
        );
        UserInfoResponse userInfoResponse = apiResponse.getData();

        assertEquals(userInfoResponse.getUsername(), userEntity.getUsername());
        assertEquals(userInfoResponse.getDescription(), userEntity.getDescription());
        assertEquals(userInfoResponse.getProfileImage(), userEntity.getProfileImage());
    }

    @Test
    @DisplayName("올바르지 않은 토큰으로 내 정보 요청시 404가 반환되어야 한다.")
    void invalid_my_info_return_valid() throws Exception {
        // given

        // when
        mockMvc.perform(
                get("/user/info")
                        .header(AUTHORIZATION, "Bearer " + accessToken + "a")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("올바른 토큰으로 내 정보 요청시 정상응답해야한다.")
    void valid_my_info_return_valid() throws Exception {
        // given

        // when
        mockMvc.perform(
                get("/user/info")
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("올바르지 않은 토큰으로 내 정보 변경시 예외가 반환된다.")
    void invalid_token_change_my_info() throws Exception {
        // given
        UserInfoEditRequest userInfoEditRequest = new UserInfoEditRequest(createRandomUUID(), createRandomUUID());
        MockMultipartFile jsonMultiPartFile = objectToMultiPart("user_info_edit", userInfoEditRequest);

        // when
        mockMvc.perform(multipart(HttpMethod.PATCH, "/user")
                .file(jsonMultiPartFile)
                .header(AUTHORIZATION, "Bearer " + accessToken + "a")
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("올바른 토큰으로 내 정보 변경시 정상으로 변경된다.")
    void valid_token_change_my_info() throws Exception {
        // given
        UserInfoEditRequest userInfoEditRequest = new UserInfoEditRequest(createRandomUUID(), createRandomUUID());
        MockMultipartFile jsonMultiPartFile = objectToMultiPart("user_info_edit", userInfoEditRequest);
        MockMultipartFile file = new MockMultipartFile("file", "abcd".getBytes(StandardCharsets.UTF_8));

        // when
        mockMvc.perform(multipart(HttpMethod.PATCH, "/user")
                .file(jsonMultiPartFile)
                .file(file)
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isNoContent());

        em.flush();
        em.clear();
        // then
        UserEntity targetUser = userRepository.findById(userEntity.getId()).orElseThrow();

        assertEquals(targetUser.getDescription(), userInfoEditRequest.getDescription());
        assertNotEquals(targetUser.getProfileImage(), userEntity.getProfileImage());
        assertEquals(targetUser.getUsername(), userInfoEditRequest.getUsername());
    }

    @Test
    @DisplayName("로그아웃 시, 액세스 토큰이 블랙리스트에 저장되어야 하고, 리프레쉬토큰은 없어져야 한다.")
    void valid_logout() throws Exception {
        // given

        // when
        mockMvc.perform(delete("/user")
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .cookie(new Cookie("RefreshToken", refreshToken))
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isNoContent());`

        // then
        assertTrue(redisManager.getValue(BLACKLIST_TOKEN, accessToken).isPresent());
        ;
        assertTrue(redisManager.getValue(REFRESH_TOKEN, refreshToken).isEmpty());
    }

    static Stream<Arguments> generateNullFieldSignUpData() {
        return Stream.of(
                Arguments.of(null, createRandomUUID(), "ABCDEFGHU", createRandomUUID()),
                Arguments.of(createRandomUUID() + "@abc.com", null, "ABCDEFGHU", createRandomUUID()),
                Arguments.of(createRandomUUID() + "@abc.com", createRandomUUID(), "ABCDEFGHU", createRandomUUID()),
                Arguments.of(createRandomUUID() + "@abc.com", createRandomUUID(), "ABCDEFGHU", null)
        );
    }
}