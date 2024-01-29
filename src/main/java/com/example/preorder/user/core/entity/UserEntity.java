package com.example.preorder.user.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import com.example.preorder.common.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.springframework.util.StringUtils.*;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 80, updatable = true)
    private String password;

    @Column(nullable = true, length = 200)
    private String description;

    @Column(nullable = true, length = 100)
    private String profileImage;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    private UserEntity(String email, String username, String password, String profileImage) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.profileImage = profileImage;
    }

    public static UserEntity createUser(String email, String name, String password, String profileImage) {
        if (!hasText(email) || !hasText(name) || !hasText(password)) {
            throw new IllegalArgumentException(
                    String.format("Args should have text. %s %s %s", email, name, password)
            );
        }
        return new UserEntity(email, name, password, profileImage);
    }

    public void changeInfo(String name, String profileImage, String description) {
        if (hasText(name) && !name.equals(this.username)) {
            this.username = name;
        }
        if (hasText(profileImage) && !profileImage.equals(this.profileImage)) {
            this.profileImage = profileImage;
        }
        if (hasText(description) && !description.equals(this.description)) {
            this.description = description;
        }
    }

    public void changeProfileImage(String fileName) {
        this.profileImage = fileName;
    }

    public void changePassword(PasswordEncoder passwordEncoder, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, this.password))
            throw new BadRequestException("Current Password is not matched.");
        this.password = passwordEncoder.encode(newPassword);
    }

    public void signOut() {
        this.deletedAt = LocalDateTime.now();
    }
}
