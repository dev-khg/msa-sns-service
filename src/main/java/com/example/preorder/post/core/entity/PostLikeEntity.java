package com.example.preorder.post.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import com.example.preorder.user.core.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Where(clause = "deleted_at is null")
@Table(name = "post_like")
public class PostLikeEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity postEntity;

    private LocalDateTime deletedAt;

    private PostLikeEntity(UserEntity userEntity, PostEntity postEntity) {
        this.userEntity = userEntity;
        this.postEntity = postEntity;
    }

    public static PostLikeEntity create(UserEntity userEntity, PostEntity postEntity) {
        return new PostLikeEntity(userEntity, postEntity);
    }

    public void handleLike(boolean like) {
        deletedAt = !like ? LocalDateTime.now() : null;
    }
}
