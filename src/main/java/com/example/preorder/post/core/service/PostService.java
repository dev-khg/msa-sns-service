package com.example.preorder.post.core.service;

import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.repository.PostRepository;
import com.example.preorder.post.presentation.request.PostCreateRequest;
import com.example.preorder.post.presentation.response.PostInfoResponse;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createPost(Long userId, PostCreateRequest postCreate) {
        UserEntity userEntity = getUserEntity(userId);

        PostEntity postEntity = PostEntity.create(userEntity.getId(), postCreate.getContent());

        return postRepository.save(postEntity).getId();
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        UserEntity userEntity = getUserEntity(userId);
        PostEntity postEntity = getPostEntity(postId);

        postEntity.deletePost(userEntity.getId());
    }

    public PostInfoResponse getPost(Long postId) {
        PostEntity postEntity = getPostEntity(postId);
        UserEntity userEntity = getUserEntity(postEntity.getUserId());

        return entityToResponse(postEntity, userEntity);
    }

    public List<PostInfoResponse> getPosts(List<Long> userId) {
        PageRequest pageable = PageRequest
                .of(0, 5, DESC, "created_at");

        return postRepository.findPostByUserIds(userId, pageable)
                .stream()
                .map(this::entityToResponse)
                .toList();
    }

    private PostInfoResponse entityToResponse(PostEntity postEntity, UserEntity userEntity) {
        return new PostInfoResponse(
                userEntity.getUsername(),
                userEntity.getId(),
                postEntity.getContent()
        );
    }

    private PostInfoResponse entityToResponse(PostEntity postEntity) {
        return new PostInfoResponse(
                null,
                postEntity.getUserId(),
                postEntity.getContent()
        );
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
