package com.example.preorder.global.security;

import com.example.preorder.user.core.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

@Getter
public class LoginUser extends User implements Authentication {
    private final UserEntity userEntity;

    public LoginUser(UserEntity userEntity) {
        super(userEntity.getEmail(), userEntity.getPassword(), new ArrayList<>());
        this.userEntity = userEntity;
    }

    @Override
    public Object getCredentials() {
        return this.userEntity;
    }

    @Override
    public Object getDetails() {
        return userEntity.getEmail();
    }

    @Override
    public Object getPrincipal() {
        return userEntity.getPassword();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return userEntity.getEmail();
    }
}
