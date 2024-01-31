package com.example.newsfeedservice.core.service;

import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.PostActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public Long enrollPost(Long userId, String content) {
        PostEntity postEntity = PostEntity.create(userId, content);

        return postRepository.save(postEntity).getId();
    }

    @Transactional(readOnly = true)
    public List<PostActivityDTO> getActivities(List<Long> targetIdList) {
        return postRepository.findPostIdList(targetIdList);
    }

    private PostActivityDTO entityToActivityResponse(PostEntity postEntity) {
        return new PostActivityDTO(
                postEntity.getUserId(), postEntity.getId(), postEntity.getContent(), postEntity.getCreatedAt()
        );
    }
}
