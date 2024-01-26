package com.example.preorder.global.security;

import com.example.preorder.user.core.entity.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import static com.example.preorder.user.core.entity.UserEntity.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    UserEntity savedUser;

    @BeforeEach
    void beforeEach() {
        savedUser = createUser(
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID(),
                createRandomUUID()
        );
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 에러를 발생해야 한다.")
    void invalid_not_match_password_throws_exception() {
        // given
        LoginUser loginUser = new LoginUser(savedUser);
        String wrongPassword = createRandomUUID();

        // when
        when(authentication.getCredentials()).thenReturn(wrongPassword);
        when(authentication.getPrincipal()).thenReturn(savedUser.getEmail());
        when(passwordEncoder.matches(wrongPassword, savedUser.getPassword()))
                .thenReturn(false);
        when(userDetailsService.loadUserByUsername(savedUser.getEmail()))
                .thenReturn(loginUser);

        // then
        assertThatThrownBy(() -> authenticationManager.authenticate(authentication))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하면 올바른 Authentication이 반환되어야 한다.")
    void valid_match_password() {
        // given
        LoginUser loginUser = new LoginUser(savedUser);
        String correctPassword = createRandomUUID();

        // when
        when(authentication.getCredentials()).thenReturn(correctPassword);
        when(authentication.getPrincipal()).thenReturn(savedUser.getEmail());
        when(passwordEncoder.matches(correctPassword, savedUser.getPassword()))
                .thenReturn(true);
        when(userDetailsService.loadUserByUsername(savedUser.getEmail()))
                .thenReturn(loginUser);

        // then
        assertEquals(authenticationManager.authenticate(authentication), loginUser);
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}