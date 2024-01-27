package com.example.preorder.post.core.event;

import com.example.preorder.common.event.EventPublisher;
import com.example.preorder.post.core.event.list.PostLikeEvent;
import com.example.preorder.post.core.event.list.PostLikeHandleEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PostLikeEventPublisher extends EventPublisher {
    public PostLikeEventPublisher(ApplicationEventPublisher publisher) {
        super(publisher);
    }

    public void handlePostLikeEvent(Long userId, Long postId, PostLikeEvent postLikeEvent) {
        publishEvent(new PostLikeHandleEvent(userId, postId, postLikeEvent));
    }
}
