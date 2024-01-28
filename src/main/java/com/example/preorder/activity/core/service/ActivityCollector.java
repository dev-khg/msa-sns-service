package com.example.preorder.activity.core.service;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityStatus;
import com.example.preorder.activity.core.entity.ActivityType;
import com.example.preorder.comment.core.service.CommentLikeService;
import com.example.preorder.comment.core.service.CommentService;
import com.example.preorder.common.activity.ActivityResponse;
import com.example.preorder.common.exception.InternalErrorException;
import com.example.preorder.follow.core.entity.FollowEntity;
import com.example.preorder.follow.core.service.FollowService;
import com.example.preorder.post.core.service.PostLikeService;
import com.example.preorder.post.core.service.PostService;
import com.example.preorder.user.core.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class ActivityCollector {
    private final FollowService followService;
    private final PostService postService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    protected void test(List<ActivityEntity> activityEntities) {

    }

    protected List<ActivityResponse> combine(ActivityType type, List<ActivityEntity> activityEntities) {
        final List<Long> targetIdList = collectTargetId(activityEntities);
        switch (type) {
            case POST -> postService.handleActivity(targetIdList);
            case FOLLOW -> followService.handleActivity(targetIdList);
            case POST_LIKE -> postLikeService.handleActivity(targetIdList);
            case COMMENT -> commentService.handleActivity(targetIdList);
            case COMMENT_LIKE -> commentLikeService.handleActivity(targetIdList);
            default -> throw new InternalErrorException("Server Error.");
        }
        return null;
    }

    protected Map<ActivityType, List<ActivityEntity>> collectMapByActivityType(List<ActivityEntity> activities) {
        return activities.stream().collect(
                groupingBy(ActivityEntity::getActivityType, toList())
        );
    }

    protected List<Long> findFolloweeList(Long userId) {
        List<FollowEntity> followerList = followService.getFollowerList(userId);

        return followerList.stream()
                .map(FollowEntity::getFollowee)
                .map(UserEntity::getId)
                .toList();
    }

    private List<Long> collectTargetId(List<ActivityEntity> activityEntities) {
        return activityEntities.stream()
                .map(ActivityEntity::getTargetId)
                .toList();
    }
}
