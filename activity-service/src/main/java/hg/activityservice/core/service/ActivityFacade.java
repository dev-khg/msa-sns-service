package hg.activityservice.core.service;

import com.example.commonproject.activity.ActivityEvent;
import com.example.commonproject.activity.ActivityType;
import hg.activityservice.core.entity.ActivityEntity;
import hg.activityservice.core.service.dto.ActivityCollector;
import hg.activityservice.core.service.external.newfeed.NewsFeedFeignClient;
import hg.activityservice.core.service.external.user.FollowerActivityRequest;
import hg.activityservice.core.service.external.user.UserFeignClient;
import hg.activityservice.infrastructure.feign.NewsFeedFeignProxy;
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
    private final NewsFeedFeignProxy newsFeedFeignClient;

    public List<ActivityResponse> getFeeds(Long userId, Pageable pageable) {
        List<Long> followerList = getFollowerList(userId);
        Map<ActivityType, List<ActivityEntity>> activities = activityService.collectActivities(followerList, pageable);

        if (activities.size() == 0) {
            return new ArrayList<>();
        }

        ActivityCollector activityCollector = new ActivityCollector(activities, userFeignClient, newsFeedFeignClient);
        List<ActivityResponse> collect = activityCollector.collect();
        collect.sort(Comparator.comparing(ActivityResponse::getCreatedAt).reversed());
        return collect;
    }

    public void handleActivityEvent(ActivityEvent event) {
        switch (event.getType()) {
            case POST_UNLIKE, COMMENT_UNLIKE:
                activityService.deleteActivity(event);
                break;
            case UNFOLLOW:
                activityService.handleUnfollow(event);
            default:
                activityService.saveActivity(event);
                break;
        }
    }

    private List<Long> getFollowerList(Long userId) {
        List<Long> body = userFeignClient.getFollowerList(new FollowerActivityRequest(userId)).getBody().getData();
        return body;
    }
}
