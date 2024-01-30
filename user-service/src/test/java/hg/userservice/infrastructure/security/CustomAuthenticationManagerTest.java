package hg.userservice.infrastructure.security;

import com.example.commonproject.exception.UnAuthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class CustomAuthenticationManagerTest {
    @InjectMocks
    CustomAuthenticationManager authenticationManager;

    @Mock
    UserDetailsService userDetailsService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    Authentication authentication;

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 에러를 발생해야 한다.")
    void invalid_not_match_password_throws_exception() {
        // given
        LoginUser loginUser = mock(LoginUser.class);
        String email = createRandomUUID();
        String encodedPassword = createRandomUUID();
        String wrongPassword = createRandomUUID();

        // when
        when(authentication.getCredentials()).thenReturn(wrongPassword);
        when(authentication.getPrincipal()).thenReturn(email);
        when(passwordEncoder.matches(wrongPassword, encodedPassword))
                .thenReturn(false);
        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(loginUser);

        // then
        assertThatThrownBy(() -> authenticationManager.authenticate(authentication))
                .isInstanceOf(UnAuthorizedException.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하면 올바른 Authentication이 반환되어야 한다.")
    void valid_match_password() {
        // given
        LoginUser loginUser = mock(LoginUser.class);

        // when
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);
        when(userDetailsService.loadUserByUsername(any()))
                .thenReturn(loginUser);

        // then
        assertEquals(authenticationManager.authenticate(authentication), loginUser);
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}