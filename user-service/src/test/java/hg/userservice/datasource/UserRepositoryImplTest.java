package hg.userservice.datasource;

import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.dto.UserNameInfoDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryImplTest {
    @Autowired
    UserRepositoryImpl userRepository;

    @PersistenceContext
    EntityManager em;

    UserEntity savedUser;

    @BeforeEach
    void beforeEach() {
        savedUser = createUserEntity(createRandomUUID(), createRandomUUID(), createRandomUUID());
        userRepository.save(savedUser);
        flushAndClearPersistence();
    }

    @Test
    @DisplayName("중복된 유니크한 필드 저장 시 예외가 반환되어야 한다.")
    void failure_user_entity_save_duplicated_field() {
        // given

        // when

        // then
        assertThatThrownBy(() ->
                userRepository.save(createUserEntity(savedUser.getEmail(), createRandomUUID(), createRandomUUID()))
        ).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() ->
                userRepository.save(createUserEntity(createRandomUUID(), savedUser.getUsername(), createRandomUUID()))
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("유저 엔티티 저장 시, 내부 값이 올바르게 설정 되어야 한다.")
    void success_user_entity_save_set_valid_field() {
        // given
        UserEntity userEntity = createUserEntity(createRandomUUID(), createRandomUUID(), createRandomUUID());
        userRepository.save(userEntity);
        flushAndClearPersistence();

        // when

        // then
        UserEntity foundUser = userRepository.findById(userEntity.getId()).orElseThrow();

        assertNotNull(foundUser.getId());
        assertEquals(foundUser.getEmail(), userEntity.getEmail());
        assertEquals(foundUser.getUsername(), userEntity.getUsername());
        assertEquals(foundUser.getPassword(), userEntity.getPassword());
        assertNull(foundUser.getProfileImage());
        assertNotNull(foundUser.getCreatedAt());
        assertNotNull(foundUser.getUpdatedAt());
        assertNull(foundUser.getDeletedAt());
    }

    @Test
    @DisplayName("이메일 존재 여부에 따라 올바른 값이 반환되어야 한다.")
    void success_user_exists_email() {
        // given
        String notExistsEmail = createRandomUUID();

        // when

        // then
        assertTrue(userRepository.existsEmail(savedUser.getEmail()));
        assertFalse(userRepository.existsEmail(notExistsEmail));
    }

    @Test
    @DisplayName("이름의 존재 여부에 따라 올바른 값이 반환되어야 한다.")
    void success_user_exists_username() {
        // given
        String notExistsUsername = createRandomUUID();

        // when

        // then
        assertTrue(userRepository.existsUsername(savedUser.getUsername()));
        assertFalse(userRepository.existsUsername(notExistsUsername));
    }

    @Test
    @DisplayName("저장된 엔티티는 ID로 조회가 가능해야 한다.")
    void success_find_user_by_exists_id() {
        // given

        // when

        // then
        assertTrue(userRepository.findById(savedUser.getId()).isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 엔티티 ID를 조회 시, 빈 객체가 반환되어야 한다.")
    void success_find_user_by_not_exists_id() {
        // given
        Long userId = 10000L;

        // when

        // then
        assertTrue(userRepository.findById(userId).isEmpty());
    }

    @Test
    @DisplayName("저장된 엔티티는 이메일로 조회가 가능해야 한다.")
    void success_find_user_by_exists_email() {
        // given

        // when

        // then
        assertTrue(userRepository.findByEmail(savedUser.getEmail()).isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 엔티티의 이메일로 조회 시, 빈 객체가 반환되어야 한다.")
    void failure_find_by_not_exists_email() {
        // given
        String notExistsEmail = createRandomUUID();

        // when

        // then
        assertTrue(userRepository.findByEmail(notExistsEmail).isEmpty());
    }

    @Test
    @DisplayName("아이디 리스트로 조회 시, 올바른 값이 반환되어야 한다.")
    void success_find_by_user_id_list() {
        // given
        int dataCount = 10;
        List<UserEntity> userList = new ArrayList<>();
        for (int i = 0; i < dataCount; i++) {
            UserEntity userEntity = createUserEntity(createRandomUUID(), createRandomUUID(), createRandomUUID());
            userRepository.save(userEntity);
            userList.add(userEntity);
        }
        flushAndClearPersistence();

        // when

        // then
        for (int i = 0; i < dataCount; i++) {
            List<Long> userIdList = userList.stream()
                    .map(UserEntity::getId)
                    .toList();

            List<UserNameInfoDTO> usernameDtoList = userRepository.findUsername(userIdList);
            List<Long> responseIdList = usernameDtoList.stream().map(UserNameInfoDTO::getId).toList();

            assertEquals(userIdList.size(), usernameDtoList.size());

            for (UserEntity userEntity : userList) {
                assertThat(responseIdList).contains(userEntity.getId());
            }
            userList.remove(0);
        }
    }

    private UserEntity createUserEntity(String email, String username, String password) {
        return UserEntity.create(email, username, password);
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }
}