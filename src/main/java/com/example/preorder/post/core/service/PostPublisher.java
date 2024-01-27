package com.example.preorder.post.core.service;

import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventPublisher;
import com.example.preorder.common.event.EventType;
import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.event.data.PostCreateData;
import com.example.preorder.post.core.repository.PostRepository;
import com.example.preorder.post.presentation.request.PostCreateRequest;
import com.example.preorder.post.presentation.response.PostInfoResponse;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.preorder.post.core.event.PostEvent.*;

@Service
public class PostPublisher extends EventPublisher {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostPublisher(ApplicationEventPublisher publisher, PostRepository postRepository, UserRepository userRepository) {
        super(publisher);
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long createPost(Long userId, String username, PostCreateRequest postCreate) {
        UserEntity userEntity = getUserEntity(userId);
        PostEntity postEntity = PostEntity.create(userEntity.getId(), username, postCreate.getContent());

        publishEvent(new DomainEvent(
                EventType.POST, POST_CREATED.ordinal(),
                new PostCreateData(userId,
                        postEntity.getId()))
        );

        return postRepository.save(postEntity).getId();
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        UserEntity userEntity = getUserEntity(userId);
        PostEntity postEntity = getPostEntity(postId);

        publishEvent(new DomainEvent(
                EventType.POST, POST_DELETED.ordinal(), postId)
        );

        postEntity.deletePost(userEntity.getId());
    }

    public PostInfoResponse getPost(Long postId) {
        PostEntity postEntity = getPostEntity(postId);

        return PostInfoResponse.of(postEntity);
    }

    public List<PostInfoResponse> getPosts(List<Long> userId, Pageable pageable) {
        return postRepository.findPostByUserIds(userId, pageable)
                .stream()
                .map(PostInfoResponse::of)
                .toList();
    }

    private PostEntity getPostEntity(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("post is not exists")
        );
    }

    private UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("please do login")
        );
    }
}
