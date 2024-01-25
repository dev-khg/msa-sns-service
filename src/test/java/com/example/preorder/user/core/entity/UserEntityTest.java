package com.example.preorder.user.core.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static com.example.preorder.user.core.entity.UserEntity.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserEntityTest {

    @Test
    @DisplayName("인스턴스 생성 시, 아이디, 비밀번호, 이름 중 어떤 필드라도 비어있으면 예외가 발생한다")
    void invalid_field_values_throws_exception() {
        // given
        String email = createRandomUUID();
        String name = createRandomUUID();
        String password = createRandomUUID();
        String profileImage = createRandomUUID();

        // when

        // then
        assertThatThrownBy(() -> createUser(null, name, password, profileImage))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> createUser(email, null, password, profileImage))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> createUser(email, name, null, profileImage))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("인스턴스 생성 시, 프로필 이미지는 비어있을 수 있다.")
    void valid_profile_filed_may_be_null() {
        // given
        String email = createRandomUUID();
        String name = createRandomUUID();
        String password = createRandomUUID();

        // when

        // then
        assertDoesNotThrow(() -> createUser(email, name, password, null));
    }

    @Test
    @DisplayName("정상적으로 인스턴스 생성 시, 필드는 입력값과 같아야 한다.")
    void valid_create_must_be_same_field_with_input() {
        // given
        String email = createRandomUUID();
        String name = createRandomUUID();
        String password = createRandomUUID();
        String profileImage = createRandomUUID();

        // when
        UserEntity userEntityWithProfileImageNull = createUser(email, name, password, null);
        UserEntity userEntityWithProfileImageNotNull = createUser(email, name, password, profileImage);

        // then
        // Case 1: User Entity's profile image is null
        assertEquals(userEntityWithProfileImageNull.getEmail(), email);
        assertEquals(userEntityWithProfileImageNull.getUsername(), name);
        assertEquals(userEntityWithProfileImageNull.getPassword(), password);
        assertEquals(userEntityWithProfileImageNull.getProfileImage(), null);

        // Case 2: User Entity's profile image is not null
        assertEquals(userEntityWithProfileImageNotNull.getEmail(), email);
        assertEquals(userEntityWithProfileImageNotNull.getUsername(), name);
        assertEquals(userEntityWithProfileImageNotNull.getPassword(), password);
        assertEquals(userEntityWithProfileImageNotNull.getProfileImage(), profileImage);
    }

    @Test
    @DisplayName("유저 정보 변경 시, null 입력을 제외한 필드만 변경되어야 한다.")
    void valid_change_info_only_not_null_field() {
        // given
        String oldName = createRandomUUID();
        String oldPassword = createRandomUUID();
        String oldProfileImage = createRandomUUID();
        UserEntity userEntity = createUser(createRandomUUID(), oldName, oldPassword, oldProfileImage);

        // then
        // Case 1: Change name
        userEntity.changeInfo(createRandomUUID(), null, null);
        assertNotEquals(oldName, userEntity.getUsername());
        assertEquals(oldPassword, userEntity.getPassword());
        assertEquals(oldProfileImage, userEntity.getProfileImage());

        // Case 2: Change password
        userEntity.changeInfo(null, createRandomUUID(), null);
        assertNotEquals(oldPassword, userEntity.getPassword());
        assertEquals(oldProfileImage, userEntity.getProfileImage());

        // Case 3: Change ProfileImage
        userEntity.changeInfo(null, null, createRandomUUID());
        assertNotEquals(oldProfileImage, userEntity.getProfileImage());
    }

    @Test
    @DisplayName("회원 탈퇴 메소드 호출 시, deletedAt 필드는 비어 있으면 안된다.")
    void valid_sign_out_user() {
        // given
        UserEntity userEntity = createUser(
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID()
        );

        // when

        // then
        assertNull(userEntity.getDeletedAt());

        userEntity.signOut();
        assertNotNull(userEntity.getDeletedAt());

    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}