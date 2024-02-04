package hg.activityservice.core.service.dto;

import com.example.commonproject.activity.ActivityType;
import hg.activityservice.core.entity.ActivityEntity;
import hg.activityservice.core.service.external.newfeed.NewsFeedFeignClient;
import hg.activityservice.core.service.external.newfeed.request.ActivityRequest;
import hg.activityservice.core.service.external.newfeed.response.CommentActivityDTO;
import hg.activityservice.core.service.external.newfeed.response.CommentLikeActivityDTO;
import hg.activityservice.core.service.external.newfeed.response.PostActivityDTO;
import hg.activityservice.core.service.external.newfeed.response.PostLikeActivityDTO;
import hg.activityservice.core.service.external.user.UserFeignClient;
import hg.activityservice.core.service.external.user.response.FollowActivityDTO;
import hg.activityservice.core.service.external.user.response.UserNameInfoDTO;
import hg.activityservice.presentation.response.*;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
public class ActivityCollector {
    private Set<Long> userIdSet = new HashSet<>();
    List<PostActivityDTO> postActivities = null;
    List<PostLikeActivityDTO> postLikeActivities = null;
    List<CommentActivityDTO> commentActivities = null;
    List<CommentLikeActivityDTO> commentLikeActivities = null;
    List<FollowActivityDTO> followActivities = null;
    private final Map<ActivityType, List<ActivityEntity>> activities;
    private final UserFeignClient userFeignClient;
    private final NewsFeedFeignClient newsFeedFeignClient;

    public List<ActivityResponse> collect() {
        for (ActivityType activityType : activities.keySet()) {
            List<ActivityEntity> activityEntities = activities.get(activityType);
            ActivityRequest activityRequest = new ActivityRequest(getTargetIdList(activityEntities));

            switch (activityType) {
                case POST -> postActivities = handlePostActivity(activityRequest);
                case POST_LIKE -> postLikeActivities = handlePostLikeActivity(activityRequest);
                case COMMENT -> commentActivities = handleCommentActivity(activityRequest);
                case COMMENT_LIKE -> commentLikeActivities = handleCommentLikeActivity(activityRequest);
                case FOLLOW -> followActivities = handleFollowActivity(activityRequest);
            }
        }

        return collectToActivityResponse();
    }

    private List<ActivityResponse> collectToActivityResponse() {
        Map<Long, String> usernameMap = handleUsername();
        List<ActivityResponse> activityResponses = new ArrayList<>();

        if (postActivities != null) {
            List<PostActivityResponse> postActivityResponses = postActivities.stream()
                    .map(p -> new PostActivityResponse(p, usernameMap.get(p.getUserId())))
                    .toList();
            activityResponses.addAll(postActivityResponses);
        }

        if (postLikeActivities != null) {
            List<PostLikeActivityResponse> postLikeActivityResponses = postLikeActivities.stream()
                    .map(p -> new PostLikeActivityResponse(p, usernameMap.get(p.getUserId()), usernameMap.get(p.getPostUserId())))
                    .toList();
            activityResponses.addAll(postLikeActivityResponses);
        }

        if (commentActivities != null) {
            List<CommentActivityResponse> commentActivityResponses = commentActivities.stream()
                    .map(c -> new CommentActivityResponse(c, usernameMap.get(c.getUserId()), usernameMap.get(c.getPostUserId())))
                    .toList();
            activityResponses.addAll(commentActivityResponses);
        }

        if (commentLikeActivities != null) {
            List<CommentLikeActivityResponse> commentLikeActivityResponses = commentLikeActivities.stream()
                    .map(c -> new CommentLikeActivityResponse(c, usernameMap.get(c.getUserId()), usernameMap.get(c.getCommentUserId())))
                    .toList();
            activityResponses.addAll(commentLikeActivityResponses);
        }

        if (followActivities != null) {
            List<FollowActivityResponse> followActivityResponses = followActivities.stream()
                    .map(FollowActivityResponse::new)
                    .toList();
            activityResponses.addAll(followActivityResponses);
        }
        return activityResponses;
    }

    private Map<Long, String> handleUsername() {
        List<UserNameInfoDTO> usernameInfos
                = userFeignClient.getUsernames(new ActivityRequest(userIdSet.stream().toList())).getBody();
        return usernameInfos.stream().collect(
                toMap(UserNameInfoDTO::getId, UserNameInfoDTO::getUsername)
        );
    }

    private List<CommentLikeActivityDTO> handleCommentLikeActivity(ActivityRequest activityRequest) {
        List<CommentLikeActivityDTO> commentLikeActivities = newsFeedFeignClient.getCommentLikeActivities(activityRequest).getBody().getData();
        List<Long> commentUserIdList = commentLikeActivities.stream()
                .map(CommentLikeActivityDTO::getCommentUserId)
                .toList();
        userIdSet.addAll(commentUserIdList);
        return commentLikeActivities;
    }

    private List<CommentActivityDTO> handleCommentActivity(ActivityRequest activityRequest) {
        List<CommentActivityDTO> commentActivities = newsFeedFeignClient.getCommentActivities(activityRequest).getBody().getData();
        List<Long> postUserIdList = commentActivities.stream()
                .map(CommentActivityDTO::getPostUserId)
                .toList();
        userIdSet.addAll(postUserIdList);
        return commentActivities;
    }

    private List<PostLikeActivityDTO> handlePostLikeActivity(ActivityRequest activityRequest) {
        List<PostLikeActivityDTO> likeActivities = newsFeedFeignClient.getPostLikeActivities(activityRequest).getBody();
        List<Long> postUserIdList = likeActivities.stream()
                .map(PostLikeActivityDTO::getPostUserId)
                .toList();
        userIdSet.addAll(postUserIdList);
        return likeActivities;
    }

    private List<PostActivityDTO> handlePostActivity(ActivityRequest activityRequest) {
        List<PostActivityDTO> postActivityDTOS = newsFeedFeignClient.getPostActivities(activityRequest).getBody();
        List<Long> userPostIdList = postActivityDTOS.stream()
                .map(p -> p.getUserId())
                .toList();
        userIdSet.addAll(userPostIdList);
        return postActivityDTOS;
    }

    private List<FollowActivityDTO> handleFollowActivity(ActivityRequest activityRequest) {
        return userFeignClient.getFollowActivities(activityRequest).getBody();
    }

    private List<Long> getTargetIdList(List<ActivityEntity> activityEntities) {
        return activityEntities.stream()
                .map(ActivityEntity::getTargetId)
                .toList();
    }

}
