package hg.userservice.core.entity;

import com.example.commonproject.exception.UnAuthorizedException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserEntity extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "number_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 80)
    private String password;

    @Column(length = 200)
    private String description;

    @Column(length = 200)
    private String profileImage;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private UserEntity(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public static UserEntity create(String email, String username, String encodedPassword) {
        return new UserEntity(email, username, encodedPassword);
    }

    public void changeProfileImage(String imageUrl) {
        this.profileImage = imageUrl;
    }

    public void changePassword(PasswordEncoder passwordEncoder, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, this.password)) {
            throw new UnAuthorizedException("Current password is not matched.");
        }

        this.password = passwordEncoder.encode(newPassword);
    }

    public void editInfo(String username, String description) {
        this.username = username;
        this.description = description;
    }
}
