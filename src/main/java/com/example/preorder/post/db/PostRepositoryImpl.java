package com.example.preorder.post.db;

import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.repository.PostRepository;
import com.example.preorder.post.db.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public Optional<PostEntity> findById(Long id) {
        return postJpaRepository.findById(id);
    }

    @Override
    public List<PostEntity> findPostByUserIds(List<Long> userIds, Pageable pageable) {
        return findPostByUserIds(userIds, pageable);
    }
}
