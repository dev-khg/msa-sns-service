package hg.activityservice.core.entity;

import com.example.commonproject.activity.ActivityType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "activity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private ActivityType type;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    private ActivityEntity(Long userId, Long targetId, ActivityType type) {
        this.userId = userId;
        this.targetId = targetId;
        this.type = type;
    }

    public static ActivityEntity create(Long userId, Long targetId, ActivityType type) {
        return new ActivityEntity(userId, targetId, type);
    }

    public void makeDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
