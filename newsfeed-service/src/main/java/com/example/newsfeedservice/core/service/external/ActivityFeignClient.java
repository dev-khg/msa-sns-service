package com.example.newsfeedservice.core.service.external;

import com.example.commonproject.activity.ActivityEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ACTIVITY-SERVICE")
public interface ActivityFeignClient {
    @PostMapping
    void handleEvent(@RequestBody ActivityEvent event);
}
