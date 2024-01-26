package com.example.preorder.user.core.service;

import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.common.utils.HttpServletUtils;
import com.example.preorder.global.jwt.TokenProvider;
import com.example.preorder.global.mail.EmailService;
import com.example.preorder.global.redis.RedisManager;
import com.example.preorder.global.storage.service.StorageService;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import com.example.preorder.user.core.resources.request.UserChangePasswordDTO;
import com.example.preorder.user.core.resources.request.UserInfoEditDTO;
import com.example.preorder.user.core.resources.request.UserSignUpDTO;
import com.example.preorder.user.core.resources.response.TokenDTO;
import com.example.preorder.user.core.resources.response.UserInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.example.preorder.common.utils.RedisKeyGenerator.RedisKeyType.*;
import static com.example.preorder.global.storage.service.StorageService.Bucket.*;
import static com.example.preorder.user.core.entity.UserEntity.*;
import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;
    @Mock
    EmailService emailService;
    @Mock
    StorageService storageService;
    @Mock
    TokenProvider tokenProvider;
    @Mock
    RedisManager redisManager;
    @Mock
    HttpServletUtils servletUtils;
    @Mock
    PasswordEncoder passwordEncoder;

    UserEntity savedEntity;

    @BeforeEach
    void beforeEach() {
        savedEntity = createUser(
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID()
        );

        settingMockRepository();
    }

    @Test
    @DisplayName("이미 존재하는 메일이나 이름으로 회원가입 시, 예외가 발생한다")
    void invalid_sign_up_duplicate_name_or_email() {
        // given
        UserSignUpDTO duplicateEmail = createSignDTO(savedEntity.getEmail(), createRandomUUID());
        UserSignUpDTO duplicateName = createSignDTO(savedEntity.getEmail(), createRandomUUID());

        // when

        // then
        assertThatThrownBy(() -> userService.signUp(duplicateEmail))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> userService.signUp(duplicateName))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("회원가입 시, 인증 코드가 일치하지 않으면 예외가 발생한다.")
    void invalid_sign_up_auth_code_throws_exception() {
        // given
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO(
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID(),
                null,
                createRandomUUID()
        );

        // when
        when(redisManager.getValue(EMAIL_CERT, userSignUpDTO.getEmail()))
                .thenReturn(ofNullable(createRandomUUID()));

        // then
        assertThatThrownBy(() -> userService.signUp(userSignUpDTO))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("정상회원 가입 시, 올바른 토큰이 반환되어야 한다.")
    void valid_sign_up() {
        // given
        String encodedPassword = createRandomUUID();
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO(
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID(),
                null,
                createRandomUUID()
        );

        // when
        when(redisManager.getValue(EMAIL_CERT, userSignUpDTO.getEmail()))
                .thenReturn(ofNullable(userSignUpDTO.getAuthCode()));
        when(tokenProvider.createAccessToken(userSignUpDTO.getEmail()))
                .thenReturn(createRandomUUID());
        when(tokenProvider.createRefreshToken(userSignUpDTO.getEmail()))
                .thenReturn(createRandomUUID());
        when(passwordEncoder.encode(userSignUpDTO.getPassword()))
                .thenReturn(encodedPassword);

        // then
        TokenDTO tokenDTO = userService.signUp(userSignUpDTO);

        assertThat(tokenDTO.getAccessToken()).isNotNull();
        assertThat(tokenDTO.getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("올바르지 않은 리프레쉬 토큰으로 재발급 요청시, 예외가 반환되어야 한다.")
    void invalid_reissue_token_with_invalid_refresh_token() {
        // given
        String correctToken = createRandomUUID();
        String nullToken = null;
        String blankToken = " ";
        String randomToken = createRandomUUID();

        // when
        when(redisManager.getValue(REFRESH_TOKEN, savedEntity.getEmail()))
                .thenReturn(ofNullable(correctToken));

        // then
        assertThatThrownBy(() -> userService.reissueToken(nullToken))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> userService.reissueToken(blankToken))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> userService.reissueToken(randomToken))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("올바른 리프레쉬 토큰으로 재발급 요청 시, 올바른 토큰이 반환되어야 한다.")
    void valid_reissue_token() {
        // given
        String correctToken = createRandomUUID();

        // when
        when(tokenProvider.getSubject(correctToken))
                .thenReturn(savedEntity.getEmail());
        when(redisManager.getValue(REFRESH_TOKEN, savedEntity.getEmail()))
                .thenReturn(ofNullable(correctToken));
        when(tokenProvider.createRefreshToken(savedEntity.getEmail()))
                .thenReturn(createRandomUUID());
        when(tokenProvider.createAccessToken(savedEntity.getEmail()))
                .thenReturn(createRandomUUID());

        // then
        TokenDTO tokenDTO = userService.reissueToken(correctToken);
        assertNotNull(tokenDTO.getRefreshToken());
        assertNotNull(tokenDTO.getAccessToken());
    }

    @ParameterizedTest
    @DisplayName("올바르지 않은 PK로 조회시 예외가 반환된다.")
    @ValueSource(longs = {1L, 2L, 3L})
    void invalid_pk_get_user_info(long pk) {
        // given

        // when
        when(userRepository.findById(any())).thenReturn(empty());

        // then
        assertThatThrownBy(() -> userService.getUserInfo(pk))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @DisplayName("올바른 PK로 조회시 정상 객체가 반환되어야 한다.")
    @ValueSource(longs = {1L})
    void valid_pk_get_user_info(long pk) {
        // given

        // when
        when(userRepository.findById(pk))
                .thenReturn(ofNullable(savedEntity));

        // then
        UserInfoDTO userInfo = userService.getUserInfo(pk);
        assertNotNull(userInfo);
        assertEquals(userInfo.getDescription(), savedEntity.getDescription());
        assertEquals(userInfo.getUsername(), savedEntity.getUsername());
        assertEquals(userInfo.getProfileImage(), savedEntity.getProfileImage());
    }

    @Test
    @DisplayName("이메일 인증 요청 시, 에러가 발생하면 안된다.")
    void valid_send_auth_code() {
        // given
        String email = createRandomUUID();

        // when
        userService.sendAuthCode(email);

        // then
        verify(redisManager, times(1))
                .putValue(any(), any(), any());
        verify(emailService, times(1))
                .sendEmail(any(), any(), any());
    }

    @ParameterizedTest
    @CsvSource({"1, 1", "1, 0", "0, 1", "0, 0"})
    @DisplayName("로그아웃 시, 액세스/리프레쉬 토큰 여부에 따라 올바른 메소드 호출이 이뤄져야 한다.")
    void valid_logout(int accessTokenIsNull, int refreshTokenIsNull) {
        // given
        String accessToken = accessTokenIsNull > 0 ? null : createRandomUUID();
        String refreshToken = refreshTokenIsNull > 0 ? null : createRandomUUID();

        // when
        if (accessTokenIsNull == 0) {
            when(tokenProvider.getSubject(accessToken)).thenReturn(savedEntity.getEmail());
        }
        when(tokenProvider.isValidateToken(anyString())).thenReturn(true);

        userService.logout(accessToken, refreshToken);

        // then
        if (accessTokenIsNull > 0) { // Case: Access Token is null
            verify(redisManager, times(0)).putValue(any(), any(), any());
        } else {
            verify(redisManager, times(1)).putValue(any(), any(), any());
        }

        if (refreshTokenIsNull > 0) { // Case: Refresh Token is null
            verify(redisManager, times(0)).deleteKey(any(), any());
        } else {
            verify(redisManager, times(1)).deleteKey(any(), any());
        }
    }

    @Test
    @DisplayName("올바르지 않은 현재 비밀번호 입력시 예외가 발생한다.")
    void invalid_password_change_throws_exception() {
        // given
        String inputPassword = createRandomUUID();
        UserChangePasswordDTO userChangePasswordDTO = new UserChangePasswordDTO(createRandomUUID(), inputPassword);

        // when
        when(userRepository.findByEmail(savedEntity.getEmail())).thenReturn(ofNullable(savedEntity));

        // then
        assertThatThrownBy(() -> userService.editPassword(userChangePasswordDTO))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("올바른 현재 비밀번호 입력시 비밀번호가 변경되어야 한다.")
    void valid_password_change() {
        // given
        String newPassword = createRandomUUID();
        UserChangePasswordDTO userChangePasswordDTO = new UserChangePasswordDTO(savedEntity.getPassword(), newPassword);

        // when
        when(servletUtils.getHeader(any()))
                .thenReturn(ofNullable(createRandomUUID()));
        when(tokenProvider.getSubject(any()))
                .thenReturn(savedEntity.getEmail());
        when(userRepository.findByEmail(savedEntity.getEmail()))
                .thenReturn(ofNullable(savedEntity));
        // then
        userService.editPassword(userChangePasswordDTO);

        assertEquals(savedEntity.getPassword(), newPassword);
    }
    
    @Test
    @DisplayName("회원정보 변경 시, 올바르게 변경되어야 한다.")
    public void valid_edit_user_info() {
        // given
        String accessToken = createRandomUUID();
        String imageFile = createRandomUUID();
        String description = createRandomUUID();
        String username = createRandomUUID();
        MultipartFile mockMultipart = mock(MultipartFile.class);

        // when
        when(mockMultipart.isEmpty()).thenReturn(false);
        when(storageService.uploadFile(mockMultipart, IMAGE))
                .thenReturn(imageFile);
        when(servletUtils.getHeader(any()))
                .thenReturn(ofNullable(accessToken));
        when(tokenProvider.getSubject(accessToken))
                .thenReturn(savedEntity.getEmail());
        when(userRepository.findByEmail(savedEntity.getEmail()))
                .thenReturn(ofNullable(savedEntity));

        //  then
        userService.editInfo(new UserInfoEditDTO(username, description, mockMultipart));

        assertEquals(savedEntity.getProfileImage(), imageFile);
        assertEquals(savedEntity.getDescription(), description);
        assertEquals(savedEntity.getUsername(), username);
    }

    private UserSignUpDTO createSignDTO(String email, String username) {
        return new UserSignUpDTO(
                email,
                username,
                createRandomUUID(),
                createRandomUUID(),
                null,
                createRandomUUID()
        );
    }

    private void settingMockRepository() {
        when(userRepository.existsUsername(savedEntity.getUsername())).thenReturn(true);
        when(userRepository.existsEmail(savedEntity.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(savedEntity.getEmail())).thenReturn(ofNullable(savedEntity));

        UserEntity any = any();
        when(userRepository.save(any)).thenReturn(any);
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}