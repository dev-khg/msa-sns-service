package hg.userservice.core.entity;

import com.example.commonproject.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "followee_id"})
}, name = "follow")
public class FollowEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "number_id", name = "follower_id", nullable = false)
    private UserEntity follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "number_id", name = "followee_id", nullable = false)
    private UserEntity followee;

    @Column
    private LocalDateTime deletedAt;

    private FollowEntity(UserEntity follower, UserEntity followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public static FollowEntity create(UserEntity follower, UserEntity followee) {
        if (follower == followee) {
            throw new BadRequestException("can not follow yourself.");
        }

        return new FollowEntity(follower, followee);
    }

    /**
     * @param mark : if true, then fill current time into deleted_at column
     *             if false, then fill null into deleted_at column
     */
    public void makeDelete(boolean mark) {
        this.deletedAt = mark ? LocalDateTime.now() : null;
    }
}
