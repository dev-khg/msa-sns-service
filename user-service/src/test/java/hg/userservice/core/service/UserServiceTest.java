package hg.userservice.core.service;

import com.example.commonproject.exception.BadRequestException;
import com.example.commonproject.exception.InternalServerException;
import com.example.commonproject.exception.UnAuthorizedException;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.KeyValueStorage;
import hg.userservice.core.repository.UserRepository;
import hg.userservice.core.vo.KeyType;
import hg.userservice.infrastructure.redis.RedisManager;
import hg.userservice.presentation.request.EditInfoRequest;
import hg.userservice.presentation.request.EditPasswordRequest;
import hg.userservice.presentation.request.SignUpRequest;
import hg.userservice.presentation.response.UserInfoResponse;
import hg.userservice.testconfiguration.EmbeddedRedisConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;

import static hg.userservice.core.entity.UserEntity.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import({EmbeddedRedisConfiguration.class})
@Transactional
class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    KeyValueStorage keyValueStorage;
    @Autowired
    UserRepository userRepository;
    @Autowired
    StringRedisTemplate redisTemplate;
    @PersistenceContext
    EntityManager em;

    UserEntity savedUser;
    UserEntity savedUserB;

    @BeforeEach
    void beforeEach() {
        savedUser = create(createRandomUUID(),
                createRandomUUID(),
                createRandomUUID()
        );
        savedUserB = create(createRandomUUID(),
                createRandomUUID(),
                createRandomUUID()
        );

        userRepository.save(savedUser);
        userRepository.save(savedUserB);

        redisTemplate.keys("*")
                .forEach(key -> redisTemplate.delete(key));

        flushAndClearPersistence();
    }

    @Test
    @DisplayName("중복된 이메일 혹은 중복된 이름으로 회원가입 시, 예외가 반환되어야 한다.")
    void failure_sign_up_with_duplicated_email_or_email() {
        //given
        UserEntity existsUser = savedUser;

        //when

        //then
        assertThatThrownBy(() ->
                userService.signUp(createSignUpForm(
                        existsUser.getEmail(), createRandomUUID(), createRandomUUID(), createRandomUUID()))
        );
        assertThatThrownBy(() ->
                userService.signUp(createSignUpForm(
                        createRandomUUID(), existsUser.getUsername(), createRandomUUID(), createRandomUUID()))
        );
    }

    @Test
    @DisplayName("인증 코드가 일치하지 않으면 예외가 발생해야 한다.")
    void failure_sign_up_invalid_auth_code() {
        //given
        int authCode = getRandomSixDigits();
        int inputCode = 0;
        while (authCode == (inputCode = getRandomSixDigits())) ;

        SignUpRequest signUpForm = createSignUpForm(
                createRandomUUID(), createRandomUUID(), createRandomUUID(), String.valueOf(inputCode)
        );
        keyValueStorage.putValue(KeyType.EMAIL_CERT, signUpForm.getEmail(), String.valueOf(authCode));

        //when

        //then
        assertThatThrownBy(
                () -> userService.signUp(signUpForm)
        ).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("정상적으로 회원가입 시, 데이터베이스에 조회가 가능해야 한다.")
    void success_sign_up_invalid_auth_code() {
        //given
        int authCode = getRandomSixDigits();

        SignUpRequest signUpForm = createSignUpForm(
                createRandomUUID(), createRandomUUID(), createRandomUUID(), String.valueOf(authCode)
        );
        keyValueStorage.putValue(KeyType.EMAIL_CERT, signUpForm.getEmail(), String.valueOf(authCode));

        //when
        userService.signUp(signUpForm);
        flushAndClearPersistence();

        //then
        assertTrue(userRepository.findByEmail(savedUser.getEmail()).isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 유저 정보 요청시, 예외가 반환되어야 한다.")
    void failure_get_user_info_by_not_exists_user_id() {
        // given

        // when

        // then
        assertThatThrownBy(
                () -> userService.getUserInfo(Long.MAX_VALUE)
        ).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("존재하는 유저 정보 요청시, 올바르게 반환되어야 한다.")
    void success_get_user_info_by_exists_user_id() {
        // given
        UserEntity existsUser = savedUser;

        // when

        // then
        UserInfoResponse userInfo = userService.getUserInfo(existsUser.getId());

        assertEquals(existsUser.getId(), userInfo.getUserId());
        assertEquals(existsUser.getUsername(), userInfo.getUsername());
    }

    @Test
    @DisplayName("존재하지 않는 유저 정보 변경 시, 예외가 반환되어야 한다.")
    void failure_edit_user_info_not_exists_user() {
        // given
        EditInfoRequest editInfoRequest = new EditInfoRequest(createRandomUUID(), createRandomUUID());

        // when

        // then
        assertThatThrownBy(
                () -> userService.editUserInfo(Long.MAX_VALUE, editInfoRequest)
        ).isInstanceOf(InternalServerException.class);
    }

    @Test
    @DisplayName("중복된 이름으로 유저 정보 변경 시, 예외가 반환되어야 한다.")
    void failure_edit_user_info_with_duplicated_user_name() {
        // given
        UserEntity existsUser = savedUser;
        UserEntity anotherUser = savedUserB;
        EditInfoRequest editInfoRequest = new EditInfoRequest(anotherUser.getUsername(), createRandomUUID());

        // when

        // then
        assertThatThrownBy(
                () -> userService.editUserInfo(existsUser.getId(), editInfoRequest)
        ).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("올바르게 유저 정보 변경 시, 정상적으로 변경되어야 한다.")
    void success_edit_user_info() {
        // given
        UserEntity existsUser = savedUser;
        EditInfoRequest editInfoRequest = new EditInfoRequest(createRandomUUID(), createRandomUUID());

        // when
        userService.editUserInfo(existsUser.getId(), editInfoRequest);
        flushAndClearPersistence();

        // then
        UserEntity changedUser = userRepository.findById(existsUser.getId()).orElseThrow();
        assertEquals(changedUser.getUsername(), editInfoRequest.getUsername());
        assertEquals(changedUser.getDescription(), editInfoRequest.getDescription());
    }

    @Test
    @DisplayName("비밀번호 변경시, 현재 비밀번호가 일치하지 않으면 예외가 반환되어야 한다.")
    void failure_change_password_invalid_current_password() {
        // given
        String realPassword = createRandomUUID();
        String encodedPassword = passwordEncoder.encode(realPassword);
        UserEntity userEntity = create(createRandomUUID(),
                createRandomUUID(),
                encodedPassword
        );
        userRepository.save(userEntity);
        flushAndClearPersistence();

        // when

        // then
        assertThatThrownBy(() ->
                userService.changePassword(
                        userEntity.getId(),
                        new EditPasswordRequest(createRandomUUID(), createRandomUUID())
                )
        ).isInstanceOf(UnAuthorizedException.class);
    }

    @Test
    @DisplayName("비밀번호 변경시, 현재 비밀번호가 일치하면 정상적으로 변경되어야 한다.")
    void success_change_password_valid_current_password() {
        // given
        String currentPassword = createRandomUUID();
        String newPassword = createRandomUUID();
        EditPasswordRequest request = new EditPasswordRequest(currentPassword, newPassword);
        UserEntity userEntity = create(createRandomUUID(),
                createRandomUUID(),
                passwordEncoder.encode(currentPassword)
        );
        userRepository.save(userEntity);
        flushAndClearPersistence();

        // when
        userService.changePassword(userEntity.getId(), request);
        flushAndClearPersistence();

        // then
        UserEntity changedUser = userRepository.findById(userEntity.getId()).orElseThrow();
        assertFalse(passwordEncoder.matches(currentPassword, changedUser.getPassword()));
        assertTrue(passwordEncoder.matches(newPassword, changedUser.getPassword()));
    }

    @Test
    @DisplayName("프로필 파일 변경 시, 이전 파일은 지워지고, 새로운 파일이 생성되어야 한다.")
    void success_change_user_profile() throws Exception{
        // given
        UserEntity existsUser = savedUser;
        MockMultipartFile mockFile = createMockMultipartFile();
        String previousPath = null;

        // when
        for (int i = 0; i < 3; i++) {
            userService.changeProfile(existsUser.getId(), mockFile);
            flushAndClearPersistence();
            Thread.sleep(500); // Wait for saving file

            UserEntity userEntity = userRepository.findById(existsUser.getId()).orElseThrow();
            if (previousPath != null) {
                assertFalse(Files.exists(Paths.get(previousPath)));
            }
            previousPath = userEntity.getProfileImage();
            assertTrue(Files.exists(Paths.get(userEntity.getProfileImage())));
        }

        // then
    }

    private int getRandomSixDigits() {
        return new Random().nextInt(899999) + 100000;
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }

    private SignUpRequest createSignUpForm(String email, String username, String password, String code) {
        return new SignUpRequest(email, username, password, code);
    }

    private MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile("file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
    }
}