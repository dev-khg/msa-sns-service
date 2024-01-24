package com.example.preorder.user.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import io.micrometer.observation.Observation;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

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
    private String name;

    @Column(nullable = false, length = 80)
    private String password;

    @Column(nullable = true, length = 100)
    private String profileImage;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    private UserEntity(String email, String name, String password, String profileImage) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.profileImage = profileImage;
    }

    public static UserEntity createUser(String email, String name, String password, String profileImage) {
        return new UserEntity(email, name, password, profileImage);
    }

    public void changeInfo(String name, String password, String profileImage) {
        if (!StringUtils.hasText(name) && !this.name.equals(name)) {
            this.name = name;
        }
        if (!StringUtils.hasText(password) && !this.password.equals(password)) {
            this.password = password;
        }
        if (!StringUtils.hasText(profileImage) && !this.profileImage.equals(profileImage)) {
            this.profileImage = profileImage;
        }
    }

    public void signOut() {
        this.deletedAt = LocalDateTime.now();
    }
}
