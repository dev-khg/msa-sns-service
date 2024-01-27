package com.example.preorder.post.core.service;

import com.example.preorder.common.exception.InternalErrorException;
import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.event.PostEventPublisher;
import com.example.preorder.post.core.repository.PostRepository;
import com.example.preorder.post.presentation.response.PostInfoResponse;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostEventPublisher eventPublisher;

    @Transactional
    public Long enrollPost(Long userId, String content) {
        UserEntity userEntity = getUserEntity(userId);
        PostEntity postEntity = PostEntity.create(userEntity, content);

        eventPublisher.handlePostCreateEvent(postEntity);

        return postRepository.save(postEntity).getId();
    }

    @Transactional(readOnly = true)
    public List<PostInfoResponse> getPostByIds(List<Long> postIdList) {
        List<PostEntity> postEntityList = postRepository.findPostIn(postIdList);

        return postEntityList.stream()
                .map(this::entityToResponse)
                .toList();
    }

    private PostInfoResponse entityToResponse(PostEntity postEntity) {
        UserEntity userEntity = postEntity.getUserEntity();
        return new PostInfoResponse(userEntity.getUsername(), postEntity.getId(), userEntity.getId());
    }

    private UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new InternalErrorException("sorry. server has problem.")
        );
    }
}
