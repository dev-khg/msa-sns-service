package hg.userservice.infrastructure.security;

import com.example.commonproject.exception.BadRequestException;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class UserDetailServiceImplTest {
    @InjectMocks
    UserDetailServiceImpl userDetailsService;

    @Mock
    UserRepository userRepository;

    UserEntity savedUser;

    @BeforeEach
    void beforeEach() {
        savedUser = UserEntity.create(createRandomUUID(), createRandomUUID(), createRandomUUID());
    }

    @Test
    @DisplayName("존재하지 않은 이메일로 처리시 예외가 발생한다.")
    void invalid_not_exists_email_throws_exception() {
        // given

        // when
        when(userRepository.findByEmail(any()))
                .thenReturn(empty());

        // then
        assertThatThrownBy(() ->
                userDetailsService.loadUserByUsername(createRandomUUID())
        ).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("존재하는 이메일로 처리시 올바른 유저 권한이 반환되어야 한다.")
    void valid_exists_email() {
        // given

        // when
        when(userRepository.findByEmail(savedUser.getEmail()))
                .thenReturn(ofNullable(savedUser));

        // then
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        assertEquals(userDetails.getUsername(), savedUser.getEmail());
        assertEquals(userDetails.getPassword(), savedUser.getPassword());
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}