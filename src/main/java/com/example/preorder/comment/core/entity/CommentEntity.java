package com.example.preorder.comment.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.user.core.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at is null")
public class CommentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity postEntity;

    @Column(nullable = false, length = 200)
    private String content;

    private LocalDateTime deletedAt;

    private CommentEntity(UserEntity userEntity, PostEntity postEntity, String content) {
        this.userEntity = userEntity;
        this.postEntity = postEntity;
        this.content = content;
    }

    public static CommentEntity create(UserEntity userEntity, PostEntity postEntity, String content) {
        return new CommentEntity(userEntity, postEntity, content);
    }

    public void deleteComment() {
        this.deletedAt = LocalDateTime.now();
    }
}
