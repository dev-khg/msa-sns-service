package com.example.preorder.post.core.service;

import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.common.exception.InternalErrorException;
import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.entity.PostLikeEntity;
import com.example.preorder.post.core.event.PostLikeEventPublisher;
import com.example.preorder.post.core.repository.PostLikeRepository;
import com.example.preorder.post.core.repository.PostRepository;
import com.example.preorder.post.presentation.response.PostLikeInfoResponse;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static com.example.preorder.post.core.event.list.PostLikeEvent.*;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeEventPublisher eventPublisher;

    @Transactional
    public void handlePostLike(Long userId, Long postId, boolean like) {
        PostLikeEntity postLike = postLikeRepository.findByUserIdAndPostId(userId, postId).orElse(null);

        if (postLike == null) {
            UserEntity userEntity = getUserEntity(userId);
            PostEntity postEntity = getPostEntity(postId);
            postLike = PostLikeEntity.create(userEntity, postEntity);
        }

        postLike.handleLike(like);
        eventPublisher.handlePostLikeEvent(userId, postId, like ? LIKE : UNLIKE);

        postLikeRepository.save(postLike);
    }

    @Transactional(readOnly = true)
    public List<PostLikeInfoResponse> findPostLikeByIdList(List<Long> postLikeIdList) {
        List<PostLikeEntity> postLikeList = postLikeRepository.findPostLikeByIdList(postLikeIdList);

        return postLikeList.stream()
                .map(PostLikeInfoResponse::new)
                .toList();
    }

    private PostEntity getPostEntity(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("Not exists post.")
        );
    }

    private UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new InternalErrorException("Server Error.")
        );
    }
}
