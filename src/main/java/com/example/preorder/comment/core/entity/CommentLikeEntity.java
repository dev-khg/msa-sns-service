package com.example.preorder.comment.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import com.example.preorder.user.core.entity.UserEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private CommentEntity commentEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(nullable = false)
    private CommentLikeStatus status;

    private CommentLikeEntity(CommentEntity commentEntity, UserEntity userEntity, CommentLikeStatus status) {
        this.commentEntity = commentEntity;
        this.userEntity = userEntity;
        this.status = status;
    }

    public static CommentLikeEntity create(CommentEntity commentEntity,
                                           UserEntity userEntity,
                                           CommentLikeStatus status) {
        return new CommentLikeEntity(commentEntity, userEntity, status);
    }

    public void changeStatus(CommentLikeStatus status) {
        this.status = status;
    }
}
