package com.example.preorder.post.core.event;

import com.example.preorder.common.event.EventPublisher;
import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.event.list.PostCreateEvent;
import com.example.preorder.post.core.event.list.PostEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends EventPublisher {
    public PostEventPublisher(ApplicationEventPublisher publisher) {
        super(publisher);
    }

    public void handlePostCreateEvent(PostEntity postEntity) {
        publishEvent(
                new PostCreateEvent(
                        PostEvent.POST_CREATED,
                        postEntity.getUserEntity().getId(),
                        postEntity.getId()
                )
        );
    }
}
