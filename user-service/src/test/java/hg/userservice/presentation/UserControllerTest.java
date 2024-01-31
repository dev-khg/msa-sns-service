package hg.userservice.presentation;

import com.example.commonproject.response.ApiResponse;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.dto.UserNameInfoDTO;
import hg.userservice.presentation.request.EditInfoRequest;
import hg.userservice.presentation.request.EditPasswordRequest;
import hg.userservice.presentation.request.UserNameInfoRequest;
import hg.userservice.presentation.response.UserInfoResponse;
import hg.userservice.testconfiguration.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static hg.userservice.core.entity.UserEntity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class UserControllerTest extends IntegrationTest {

    @Test
    @DisplayName("존재하지 않는 유저 정보 요청시, BadRequest가 반환되어야 한다.")
    void get_user_info_with_not_exists_user_id() throws Exception {
        // given
        Long notExistsUserId = Long.MAX_VALUE;

        // when
        mockMvc.perform(
                get("/user/" + notExistsUserId)
        ).andExpect(status().isBadRequest());

        // then
    }

    @Test
    @DisplayName("존재하는 유저 정보 요청시, 올바른 값이 반환되어야 한다.")
    void get_user_info_with_exists_user_id() throws Exception {
        // given
        UserEntity existsUser = saveUserList.get(0);

        // when
        MvcResult mvcResult = mockMvc.perform(
                get("/user/" + existsUser.getId())).andExpect(status().isOk()
        ).andReturn();

        // then
        UserInfoResponse userInfoResponse =
                readResponseJsonBody(mvcResult.getResponse().getContentAsString(), UserInfoResponse.class);

        assertEquals(userInfoResponse.getUserId(), existsUser.getId());
        assertEquals(userInfoResponse.getUsername(), existsUser.getUsername());
        assertEquals(userInfoResponse.getDescription(), existsUser.getDescription());
        assertEquals(userInfoResponse.getProfileImage(), existsUser.getProfileImage());
    }

    @Test
    @DisplayName("올바르지 않은 토큰으로 내 정보 요청 시, 예외가 반환되어야 한다.")
    void get_my_info_with_invalid_access_token() throws Exception {
        // given
        UserEntity me = saveUserList.get(0);
        String accessToken = tokenProvider.createAccessToken(String.valueOf(me.getId()));

        // when

        mockMvc.perform(get("/user/me")
                .header(HttpHeaders.AUTHORIZATION, accessToken + "a")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("올바른 토큰으로 내 정보 요청 시, 올바른 값이 반환되어야 한다.")
    void get_my_info_with_access_token() throws Exception {
        // given
        UserEntity me = saveUserList.get(0);
        String accessToken = tokenProvider.createAccessToken(String.valueOf(me.getId()));

        // when
        MvcResult mvcResult = mockMvc.perform(get("/user/me")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                ).andExpect(status().isOk())
                .andReturn();

        // then
        UserInfoResponse userInfoResponse
                = readResponseJsonBody(mvcResult.getResponse().getContentAsString(), UserInfoResponse.class);

        assertEquals(userInfoResponse.getUserId(), me.getId());
        assertEquals(userInfoResponse.getUsername(), me.getUsername());
        assertEquals(userInfoResponse.getDescription(), me.getDescription());
        assertEquals(userInfoResponse.getProfileImage(), me.getProfileImage());
    }

    @Test
    @DisplayName("올바르지 않은 토큰으로 내 정보 변경 시, 예외가 반환되어야 한다.")
    void change_my_info_with_invalid_access_token() throws Exception {
        // given
        UserEntity me = saveUserList.get(0);
        String accessToken = tokenProvider.createAccessToken(String.valueOf(me.getId()));
        EditInfoRequest editInfoRequest = new EditInfoRequest(createRandomUUID(), createRandomUUID());

        // when
        mockMvc.perform(put("/user/me")
                .header(HttpHeaders.AUTHORIZATION, accessToken + "a")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(editInfoRequest))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("올바른 토큰으로 내 정보 변경 시, 정상적으로 변경되어야 한다.")
    void change_my_info_with_valid_access_token() throws Exception {
        // given
        UserEntity me = saveUserList.get(0);
        String accessToken = tokenProvider.createAccessToken(String.valueOf(me.getId()));
        EditInfoRequest editInfoRequest = new EditInfoRequest(createRandomUUID(), createRandomUUID());

        // when
        mockMvc.perform(put("/user/me")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(editInfoRequest))
        ).andExpect(status().isNoContent());
        flushAndClearPersistence();

        // then
        UserEntity userEntity = userRepository.findById(me.getId()).orElseThrow();

        assertEquals(userEntity.getUsername(), editInfoRequest.getUsername());
        assertEquals(userEntity.getDescription(), editInfoRequest.getDescription());
    }

    @Test
    @DisplayName("올바르지 않은 토큰으로 비밀번호 변경 시, 예외가 반환된다.")
    void change_password_with_invalid_password() throws Exception {
        // given
        String currentPassword = "ABDEFGHIJK";
        UserEntity userEntity = create(createRandomUUID(), "AGASGASGH", passwordEncoder.encode(currentPassword));

        userRepository.save(userEntity);
        flushAndClearPersistence();
        String accessToken = tokenProvider.createAccessToken(String.valueOf(userEntity.getId()));
        EditPasswordRequest passwordRequest = new EditPasswordRequest(currentPassword, "ABCDEFGHIJK");

        // when
        mockMvc.perform(patch("/user/me/password")
                .header(HttpHeaders.AUTHORIZATION, accessToken + "a")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(passwordRequest))
        ).andExpect(status().isUnauthorized());
        // then
    }

    @Test
    @DisplayName("올바른 토큰과 현재 비밀번호로 비밀번호 변경 시, 정상적으로 변경된다.")
    void change_password_with_valid_password() throws Exception {
        // given
        String currentPassword = "ABDEFGHIJK";
        String newPassword = "ABDEFGHIJKEWD";

        UserEntity userEntity = create(createRandomUUID(), "AGASGASGH", passwordEncoder.encode(currentPassword));

        userRepository.save(userEntity);
        flushAndClearPersistence();
        String accessToken = tokenProvider.createAccessToken(String.valueOf(userEntity.getId()));
        EditPasswordRequest passwordRequest = new EditPasswordRequest(currentPassword, newPassword);

        // when
        mockMvc.perform(patch("/user/me/password")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(passwordRequest))
        ).andExpect(status().isNoContent());
        flushAndClearPersistence();

        // then
        UserEntity changedUser = userRepository.findById(userEntity.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, changedUser.getPassword()));
    }

    @Test
    @DisplayName("프로필 이미지 변경 시, 파일이 경로에 존재해야 한다.")
    void change_profile_image() throws Exception {
        // given
        UserEntity userEntity = saveUserList.get(0);
        String accessToken = tokenProvider.createAccessToken(String.valueOf(userEntity.getId()));

        // when
        mockMvc.perform(multipart(PATCH, "/user/me/profile")
                .file(createMockMultipartFile())
                .contentType(MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andExpect(status().isNoContent());
        flushAndClearPersistence();

        // then
        UserEntity changedUser = userRepository.findById(userEntity.getId()).orElseThrow();
        assertTrue(Files.exists(Paths.get(changedUser.getProfileImage())));
        Files.delete(Paths.get(changedUser.getProfileImage()));
    }

    @Test
    @DisplayName("존재하는 유저 PK로 유저이름 요청시 올바르게 반환되어야 한다.")
    void getUsername_with_user_pk_list() throws Exception {
        //given
        List<Long> userIdList = saveUserList.stream()
                .map(UserEntity::getId)
                .toList();
        UserNameInfoRequest userNameInfoRequest = new UserNameInfoRequest(userIdList);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/user/name")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userNameInfoRequest))
                ).andExpect(status().isOk())
                .andReturn();

        // then
        UserNameInfoDTO[] userNameInfoDTOS =
                readResponseJsonBody(mvcResult.getResponse().getContentAsString(), UserNameInfoDTO[].class);

        for (UserNameInfoDTO userNameInfoDTO : userNameInfoDTOS) {
            UserEntity samePk = saveUserList.stream()
                    .filter(u -> userNameInfoDTO.getId().equals(u.getId()))
                    .findAny()
                    .orElseThrow();

            assertEquals(samePk.getUsername(), userNameInfoDTO.getUsername());
        }
    }

    private MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile("file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
    }
}