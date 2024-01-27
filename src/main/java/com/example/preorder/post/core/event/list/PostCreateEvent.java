package com.example.preorder.post.core.event.list;

import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PostCreateEvent extends DomainEvent implements Serializable {
    private final PostEvent postEvent;
    private final Long userId;
    private final Long postId;

    public PostCreateEvent(PostEvent postEvent, Long userId, Long postId) {
        super(EventType.POST);
        this.postEvent = postEvent;
        this.userId = userId;
        this.postId = postId;
    }
}
