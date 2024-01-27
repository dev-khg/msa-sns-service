package com.example.preorder.comment.core.event;

import com.example.preorder.comment.core.event.list.CommentDomainEvent;
import com.example.preorder.comment.core.event.list.CommentEvent;
import com.example.preorder.common.event.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static com.example.preorder.comment.core.event.list.CommentEvent.*;

@Component
public class CommentEventPublisher extends EventPublisher {
    public CommentEventPublisher(ApplicationEventPublisher publisher) {
        super(publisher);
    }

    public void handleEnrollComment(Long userId, Long commentId) {
        CommentDomainEvent commentDomainEvent = new CommentDomainEvent(commentId, userId, ENROLL_COMMENT);
        publishEvent(commentDomainEvent);
    }
}
