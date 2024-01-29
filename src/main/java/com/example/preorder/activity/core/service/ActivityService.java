package com.example.preorder.activity.core.service;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityType;
import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.common.activity.ActivityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.preorder.activity.core.entity.ActivityStatus.*;
import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityCollector activityCollector;

    public List<ActivityResponse> getActivities(Long userId, Pageable pageable) {
        List<Long> followeeList = activityCollector.findFolloweeList(userId);
        List<ActivityEntity> activities = getActivities(followeeList, pageable);

        Map<ActivityType, List<ActivityEntity>> collect = activityCollector.collectMapByActivityType(activities);

        List<ActivityResponse> activityResponses = new ArrayList<>(activities.size());

        for (ActivityType activityType : collect.keySet()) {
            activityResponses.addAll(activityCollector.combine(activityType, collect.get(activityType)));
        }

        activityResponses.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return activityResponses;
    }

    private Map<ActivityType, List<ActivityEntity>> collectMapByActivityType(List<ActivityEntity> activities) {
        return activities.stream().collect(
                groupingBy(ActivityEntity::getActivityType, toList())
        );
    }

    private List<ActivityEntity> getActivities(List<Long> followerList, Pageable pageable) {
        return activityRepository.findValidActivitiesByUserId(followerList, VALID, pageable);
    }
}
