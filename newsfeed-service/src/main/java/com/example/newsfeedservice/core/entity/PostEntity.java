package com.example.newsfeedservice.core.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
public class PostEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime deletedAt;

    private PostEntity(Long userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public static PostEntity create(Long userId, String content) {
        return new PostEntity(userId, content);
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
