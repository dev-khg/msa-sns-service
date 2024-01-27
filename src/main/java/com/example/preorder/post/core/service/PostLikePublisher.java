package com.example.preorder.post.core.service;

import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventPublisher;
import com.example.preorder.common.event.EventType;
import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.entity.PostLikeEntity;
import com.example.preorder.post.core.event.PostEvent;
import com.example.preorder.post.core.repository.PostLikeRepository;
import com.example.preorder.post.core.repository.PostRepository;
import com.example.preorder.post.core.vo.PostLikeStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikePublisher extends EventPublisher {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    public PostLikePublisher(ApplicationEventPublisher publisher, PostLikeRepository postLikeRepository, PostRepository postRepository) {
        super(publisher);
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public void createPostLike(Long userId, Long postId, String username, PostLikeStatus status) {
        PostEntity postEntity = getPostEntity(postId);
        PostLikeEntity postLikeEntity = postLikeRepository.findByUserIdAndPostId(userId, postId)
                .orElse(null);

        if (postLikeEntity == null) {
            postLikeEntity = PostLikeEntity.create(userId, postEntity.getId(), username, status);
        }

        postLikeEntity.changeStatus(userId, status);

        publishEvent(new DomainEvent(
                EventType.POST,
                PostEvent.POST_LIKE.ordinal(),
                postLikeEntity)
        );

        postLikeRepository.save(postLikeEntity);
    }

    private PostEntity getPostEntity(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("Not found post.")
        );
    }
}
