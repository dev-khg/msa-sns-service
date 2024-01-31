package com.example.newsfeedservice.core.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_like")
public class CommentLikeEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity commentEntity;

    private LocalDateTime deletedAt;

    private CommentLikeEntity(Long userId, CommentEntity commentEntity) {
        this.userId = userId;
        this.commentEntity = commentEntity;
    }

    public static CommentLikeEntity create(Long userId, CommentEntity commentEntity) {
        return new CommentLikeEntity(userId, commentEntity);
    }

    /**
     * @param mark : if true, then fill current time into deleted_at column
     *             if false, then fill null into deleted_at column
     */
    public void makeDelete(boolean mark) {
        this.deletedAt = mark ? LocalDateTime.now() : null;
    }
}
