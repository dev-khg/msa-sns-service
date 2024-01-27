package com.example.preorder.post.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.post.core.vo.PostStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Where(clause = "deleted_at is null")
public class PostEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private PostStatus status;

    private LocalDateTime deletedAt;

    public PostEntity(Long userId, String content, PostStatus status) {
        this.userId = userId;
        this.content = content;
        this.status = status;
    }

    public static PostEntity create(Long userId, String content) {
        return new PostEntity(userId, content, PostStatus.ACTIVE);
    }

    public void deletePost(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new BadRequestException("please do login");
        }
        this.deletedAt = LocalDateTime.now();
    }
}
