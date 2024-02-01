package hg.activityservice.core.service;

import com.example.commonproject.activity.ActivityEvent;
import com.example.commonproject.activity.ActivityType;
import hg.activityservice.core.entity.ActivityEntity;
import hg.activityservice.core.repository.ActivityRepository;
import hg.activityservice.core.service.external.newfeed.NewsFeedFeignClient;
import hg.activityservice.core.service.external.user.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    public Map<ActivityType, List<ActivityEntity>> collectActivities(List<Long> followerList, Pageable pageable) {
        List<ActivityEntity> activities = activityRepository.findActivitiesByUserIdIn(followerList, pageable);
        return collectByType(activities);
    }

    @Transactional
    public void deleteActivity(ActivityEvent event) {
        activityRepository.findByUserIdAndTargetIdAndType(event.getUserId(), event.getTargetId(), event.getType()).ifPresent(
                activity -> activity.makeDelete()
        );
    }

    @Transactional
    public void saveActivity(ActivityEvent event) {
        ActivityEntity activityEntity = ActivityEntity.create(event.getUserId(), event.getTargetId(), event.getType());
        activityRepository.save(activityEntity);
    }

    private Map<ActivityType, List<ActivityEntity>> collectByType(List<ActivityEntity> activities) {
        return activities.stream().collect(
                Collectors.groupingBy(ActivityEntity::getType, Collectors.toList())
        );
    }
}
