package com.example.newsfeedservice.core.service;

import com.example.commonproject.activity.ActivityEvent;
import com.example.commonproject.event.EventPublisher;
import com.example.newsfeedservice.core.service.external.ActivityFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.example.commonproject.event.Topic.ACTIVITY_EVENT;

@Service
@RequiredArgsConstructor
public class KafkaPublisher implements EventPublisher {
    private final ActivityFeignClient activityFeignClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(ActivityEvent event) {
        try {
            String content = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(ACTIVITY_EVENT, content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fallback(ActivityEvent event, Exception e) {
        activityFeignClient.handleEvent(event);
    }
}
