package com.example.preorder.user.db;

import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.preorder.user.core.entity.UserEntity.*;
import static com.example.preorder.utils.AssertUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryImplTest {
    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    EntityManager em;

    UserEntity savedUser;

    @BeforeEach
    void beforeEach() {
        savedUser = createUser(createRandomUUID(), createRandomUUID(), createRandomUUID(), createRandomUUID());
        userRepository.save(savedUser);
        clearAndFlushPersistence();
    }

    @Test
    @DisplayName("이메일 조회 시, 존재 여부에 따라 유효한 값이 나와야 한다.")
    void validation_exists_email() {
        assertTrue(userRepository.existsEmail(savedUser.getEmail()));
        assertFalse(userRepository.existsEmail(createRandomUUID()));
    }

    @Test
    @DisplayName("이름 조회 시, 존재 여부에 따라 유효한 값이 나와야 한다.")
    void validation_exists_username() {
        assertTrue(userRepository.existsUsername(savedUser.getUsername()));
        assertFalse(userRepository.existsUsername(createRandomUUID()));
    }

    @Test
    @DisplayName("이메일로 엔티티 조회시, 존재 여부에 따라 올바른 값이 반환 되어야 한다.")
    void validation_find_entity_by_email() {
        assertThat(userRepository.findByEmail(createRandomUUID())).isEmpty();
        assertThat(userRepository.findByEmail(savedUser.getEmail())).isPresent();
    }

    @Test
    @DisplayName("PK로 조회 시, 존재 여부에 따라 올바른 값이 반환 되어야 한다.")
    void validation_find_entity_by_pk() {
        assertThat(userRepository.findById(100L)).isEmpty();
        assertThat(userRepository.findById(savedUser.getId())).isPresent();
    }

    @Test
    @DisplayName("엔티티 저장 시, 값이 올바르게 설정되어 있어야 한다.")
    void validation_save_entity() {
        // given
        UserEntity userEntity = createUser(
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID()
        );

        // when
        UserEntity savedEntity = userRepository.save(userEntity);

        // then
        assertEquals(savedEntity.getProfileImage(), userEntity.getProfileImage());
        assertEquals(savedEntity.getPassword(), userEntity.getPassword());
        assertEquals(savedEntity.getEmail(), userEntity.getEmail());
        assertEquals(savedEntity.getUsername(), userEntity.getUsername());
        assertNotNulls(savedEntity.getCreatedAt(), savedEntity.getId(), savedEntity.getUpdatedAt());
        assertNull(savedEntity.getDeletedAt());
    }

    private void clearAndFlushPersistence() {
        em.flush();
        em.clear();
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}