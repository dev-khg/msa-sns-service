package com.example.preorder.comment.core.event.list;

import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import lombok.Getter;

@Getter
public class CommentLikeDomainEvent extends DomainEvent {
    private final Long userId;
    private final Long commentId;
    private final CommentLikeEvent event;

    public CommentLikeDomainEvent(Long userId, Long commentId, CommentLikeEvent event) {
        super(EventType.COMMENT_LIKE);
        this.userId = userId;
        this.commentId = commentId;
        this.event = event;
    }
}
