package com.example.preorder.post.core.event.list;

import com.example.preorder.common.event.DomainEvent;
import lombok.Getter;

import static com.example.preorder.common.event.EventType.*;

@Getter
public class PostLikeDomainEvent extends DomainEvent {
    private final Long userId;
    private final Long postId;
    private final PostLikeEvent event;

    public PostLikeDomainEvent(Long userId, Long postId, PostLikeEvent event) {
        super(POST_LIKE);
        this.userId = userId;
        this.postId = postId;
        this.event = event;
    }
}
