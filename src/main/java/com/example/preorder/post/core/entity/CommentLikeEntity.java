package com.example.preorder.post.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import com.example.preorder.post.core.vo.CommentLikeStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLikeEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Long commentId;

    @Column(nullable = false)
    private CommentLikeStatus status;

    private CommentLikeEntity(Long userId, String username, Long commentId, CommentLikeStatus status) {
        this.userId = userId;
        this.commentId = commentId;
        this.username = username;
        this.status = status;
    }

    public static CommentLikeEntity create(Long userId, Long commentId, String username, CommentLikeStatus status) {
        return new CommentLikeEntity(userId, username, commentId, status);
    }

    public void changeStatus(CommentLikeStatus status) {
        this.status = status;
    }
}
