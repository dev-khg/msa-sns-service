package com.example.preorder.user.db;

import com.example.preorder.user.core.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    @Query("select u.username from UserEntity u where u.id = :userId")
    String findUsernameByUserId(@Param("userId") Long userId);
}
