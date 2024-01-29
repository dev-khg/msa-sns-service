package com.example.preorder.user.core.repository;

import com.example.preorder.user.core.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findById(Long id);

    boolean existsEmail(String email);

    boolean existsUsername(String username);

    UserEntity save(UserEntity userEntity);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAll();
}
