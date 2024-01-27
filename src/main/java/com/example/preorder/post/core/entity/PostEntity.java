package com.example.preorder.post.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
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
@Table(name = "post")
@Where(clause = "deleted_at is null")
public class PostEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime deletedAt;

    private PostEntity(UserEntity userEntity, String content) {
        this.userEntity = userEntity;
        this.content = content;
    }

    public static PostEntity create(UserEntity userEntity, String content) {
        return new PostEntity(userEntity, content);
    }
}
