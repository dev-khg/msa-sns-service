package hg.userservice.infrastructure.kafka;

import com.example.commonproject.activity.ActivityEvent;
import com.example.commonproject.activity.ActivityType;
import com.example.commonproject.event.EventPublisher;
import com.example.commonproject.event.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hg.userservice.core.service.external.ActivityFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.example.commonproject.event.Topic.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPublisher implements EventPublisher {
    private final ActivityFeignClient activityFeignClient;
    private final KafkaTemplate<String, ActivityEvent> kafkaTemplate;

    @Override
    public void publish(ActivityEvent event) {
        log.info("[KAFKA] =======> Event [{}] User [{}] Target [{}]", event.getType(), event.getUserId(), event.getTargetId());
        kafkaTemplate.send(ACTIVITY_EVENT, event);
    }

    @Override
    public void fallback(ActivityEvent event, Exception e) {
        log.info("[HTTP] =======> Event [{}] User [{}] Target [{}]", event.getType(), event.getUserId(), event.getTargetId());
        activityFeignClient.handleEvent(event);
    }
}
