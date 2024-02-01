package hg.activityservice.core.service;

import com.example.commonproject.activity.ActivityEvent;
import com.example.commonproject.activity.ActivityType;
import hg.activityservice.core.entity.ActivityEntity;
import hg.activityservice.core.service.dto.ActivityCollector;
import hg.activityservice.core.service.external.newfeed.NewsFeedFeignClient;
import hg.activityservice.core.service.external.user.UserFeignClient;
import hg.activityservice.presentation.response.ActivityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityFacade {
    private final ActivityService activityService;
    private final UserFeignClient userFeignClient;
    private final NewsFeedFeignClient newsFeedFeignClient;

    public List<ActivityResponse> getFeeds(Long userId, Pageable pageable) {
        List<Long> followerList = getFollowerList(userId);
        Map<ActivityType, List<ActivityEntity>> activities = activityService.collectActivities(followerList, pageable);

        if (activities.size() == 0) {
            return new ArrayList<>();
        }

        ActivityCollector activityCollector = new ActivityCollector(activities, userFeignClient, newsFeedFeignClient);
        List<ActivityResponse> collect = activityCollector.collect();
        collect.sort(Comparator.comparing(ActivityResponse::getCreatedAt));
        return collect;
    }

    public void handleActivityEvent(ActivityEvent event) {
        switch (event.getType()) {
            case POST_UNLIKE, COMMENT_UNLIKE:
                activityService.deleteActivity(event);
                break;
            default:
                activityService.saveActivity(event);
                break;
        }
    }

    private List<Long> getFollowerList(Long userId) {
        return userFeignClient.getFollowerList(userId).getBody();
    }
}
