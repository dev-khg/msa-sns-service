package com.example.preorder.comment.core.event;

import com.example.preorder.comment.core.event.list.CommentLikeDomainEvent;
import com.example.preorder.comment.core.event.list.CommentLikeEvent;
import com.example.preorder.common.event.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CommentLikeEventPublisher extends EventPublisher {
    public CommentLikeEventPublisher(ApplicationEventPublisher publisher) {
        super(publisher);
    }

    public void commentLikeHandle(Long userId, Long commentId, CommentLikeEvent event) {
        publishEvent(new CommentLikeDomainEvent(userId, commentId, event));
    }
}
