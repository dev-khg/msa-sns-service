package hg.userservice.infrastructure.kafka;

import com.example.commonproject.activity.ActivityEvent;
import com.example.commonproject.activity.ActivityType;
import com.example.commonproject.event.EventPublisher;
import com.example.commonproject.event.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hg.userservice.core.service.external.ActivityFeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @CircuitBreaker(name = "activityCircuitBreaker", fallbackMethod = "fallback")
    public void publish(ActivityEvent event) {
        try {
            String content = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(ACTIVITY_EVENT, content);
            log.info("[KAFKA] =======> Event [{}] User [{}] Target [{}]", event.getType(), event.getUserId(), event.getTargetId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fallback(ActivityEvent event, Exception e) {
        log.info("[HTTP] =======> Event [{}] User [{}] Target [{}]", event.getType(), event.getUserId(), event.getTargetId());
        activityFeignClient.handleEvent(event);
    }
}
