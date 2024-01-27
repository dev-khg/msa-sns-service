package com.example.preorder.post.db;

import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository postJpaRepository;

    @Override
    public PostEntity save(PostEntity postEntity) {
        return postJpaRepository.save(postEntity);
    }

    @Override
    public Optional<PostEntity> findById(Long postId) {
        return postJpaRepository.findById(postId);
    }

    @Override
    public Optional<PostEntity> findByIdFJUser(Long postId) {
        return postJpaRepository.findByIdFJUser(postId);
    }

    public List<PostEntity> findPostIn(List<Long> postIdList) {
        return postJpaRepository.findAllById(postIdList);
    }
}
