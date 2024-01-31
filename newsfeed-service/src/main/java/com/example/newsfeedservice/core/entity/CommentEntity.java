package com.example.newsfeedservice.core.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity postEntity;

    @Column(nullable = false)
    private String content;

    private LocalDateTime deletedAt;

    private CommentEntity(Long userId, PostEntity postEntity, String content) {
        this.userId = userId;
        this.postEntity = postEntity;
        this.content = content;
    }

    public static CommentEntity create(Long userId, PostEntity postEntity, String content) {
        return new CommentEntity(userId, postEntity, content);
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
