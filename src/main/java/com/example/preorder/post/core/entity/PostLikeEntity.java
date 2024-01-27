package com.example.preorder.post.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.post.core.vo.PostLikeStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.preorder.post.core.vo.PostLikeStatus.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLikeEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private PostLikeStatus status;

    private PostLikeEntity(Long userId, Long postId, PostLikeStatus status) {
        this.userId = userId;
        this.postId = postId;
    }

    public static PostLikeEntity create(Long userId, Long postId, PostLikeStatus status) {
        return new PostLikeEntity(userId, postId, status);
    }

    public void changeStatus(Long userId, PostLikeStatus status) {
        if(!this.userId.equals(userId)) {
            throw new BadRequestException("doesn't have authority");
        }

        this.status = status;
    }
}
