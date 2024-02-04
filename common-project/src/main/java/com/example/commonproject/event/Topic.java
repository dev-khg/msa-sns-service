package com.example.commonproject.event;

import com.example.commonproject.activity.ActivityType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Topic {
    public static final String ACTIVITY_EVENT = "ACTIVITY_EVENT";
//    public static final String FOLLOW_CREATED = "FOLLOW_CREATED";
//    public static final String UNFOLLOW_UPDATED = "UNFOLLOW_UPDATED";
//    public static final String COMMENT_CREATED = "COMMENT_CREATED";
//    public static final String COMMENT_LIKE_CREATED = "COMMENT_LIKE_CREATED";
//    public static final String COMMENT_UNLIKE_UPDATED = "COMMENT_UNLIKE_UPDATED";
//    public static final String POST_CREATED = "POST_CREATED";
//    public static final String POST_LIKE_CREATED = "POST_LIKE_CREATED";
//    public static final String POST_UNLIKE_UPDATED = "POST_UNLIKE_UPDATED";
//
//    public static String getTopic(ActivityType type) {
//        return switch (type) {
//            case COMMENT -> Topic.COMMENT_CREATED;
//            case COMMENT_LIKE -> Topic.COMMENT_LIKE_CREATED;
//            case COMMENT_UNLIKE -> Topic.COMMENT_UNLIKE_UPDATED;
//            case POST_UNLIKE -> Topic.POST_UNLIKE_UPDATED;
//            case POST_LIKE -> Topic.POST_LIKE_CREATED;
//            case POST -> Topic.POST_CREATED;
//            case FOLLOW -> Topic.FOLLOW_CREATED;
//            case UNFOLLOW -> Topic.UNFOLLOW_UPDATED;
//        };
//    }
}
