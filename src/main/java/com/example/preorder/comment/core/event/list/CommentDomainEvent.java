package com.example.preorder.comment.core.event.list;

import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CommentDomainEvent extends DomainEvent {
    private final Long commentId;
    private final Long userId;
    private final CommentEvent event;

    public CommentDomainEvent(Long commentId, Long userId, CommentEvent event) {
        super(EventType.COMMENT);
        this.commentId = commentId;
        this.userId = userId;
        this.event = event;
    }
}
