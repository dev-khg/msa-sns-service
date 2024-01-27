package com.example.preorder.post.core.service;

import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.entity.PostLikeEntity;
import com.example.preorder.post.core.repository.PostLikeRepository;
import com.example.preorder.post.core.repository.PostRepository;
import com.example.preorder.post.core.vo.PostLikeStatus;
import com.example.preorder.post.db.dto.PostDto;
import com.example.preorder.post.presentation.response.PostLikeResponse;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import com.example.preorder.user.db.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.preorder.post.core.vo.PostLikeStatus.*;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createPostLike(Long userId, Long postId, PostLikeStatus status) {
        PostEntity postEntity = getPostEntity(postId);
        UserEntity userEntity = getUserEntity(userId);

        PostLikeEntity postLikeEntity = postLikeRepository.findByUserIdAndPostId(userId, postId)
                .orElse(null);

        if (postLikeEntity == null) {
            postLikeEntity = PostLikeEntity.create(userId, postId, status);
        }

        postLikeEntity.changeStatus(userId, status);

        postLikeRepository.save(postLikeEntity);
    }

    private UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("Not found user.")
        );
    }

    private PostEntity getPostEntity(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("Not found post.")
        );
    }
}
