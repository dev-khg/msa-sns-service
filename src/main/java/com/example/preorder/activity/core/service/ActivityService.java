package com.example.preorder.activity.core.service;

import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.common.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;

    @TransactionalEventListener
    public void handleEvent(Event event) {

    }
}
