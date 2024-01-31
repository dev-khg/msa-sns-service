package com.example.newsfeedservice.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_like")
public class PostLikeEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity postEntity;

    private LocalDateTime deletedAt;

    private PostLikeEntity(Long userId, PostEntity postEntity) {
        this.userId = userId;
        this.postEntity = postEntity;
    }

    public static PostLikeEntity create(Long userId, PostEntity postEntity) {
        return new PostLikeEntity(userId, postEntity);
    }

    /**
     * @param mark : if true, then fill current time into deleted_at column
     *             if false, then fill null into deleted_at column
     */
    public void makeDelete(boolean mark) {
        this.deletedAt = mark ? LocalDateTime.now() : null;
    }
}
