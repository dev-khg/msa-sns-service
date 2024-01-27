package com.example.preorder.post.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at is null")
public class CommentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long postId;

    @Column(length = 200, nullable = false)
    private String content;

    private String username;

    private LocalDateTime deletedAt;

    private CommentEntity(Long userId, Long postId, String content, String username) {
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.username = username;
    }

    public static CommentEntity create(Long userId, Long postId, String username, String content) {
        return new CommentEntity(userId, postId, username, content);
    }
}
