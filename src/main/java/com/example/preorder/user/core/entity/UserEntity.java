package com.example.preorder.user.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static org.springframework.util.StringUtils.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 80)
    private String password;

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

    public void changeInfo(String name, String password, String profileImage) {
        if (hasText(name) && !this.username.equals(name)) {
            this.username = name;
        }
        if (hasText(password) && !this.password.equals(password)) {
            this.password = password;
        }
        if (hasText(profileImage) && !this.profileImage.equals(profileImage)) {
            this.profileImage = profileImage;
        }
    }

    public void signOut() {
        this.deletedAt = LocalDateTime.now();
    }
}
