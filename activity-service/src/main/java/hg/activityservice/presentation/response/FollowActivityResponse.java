package hg.activityservice.presentation.response;

import hg.activityservice.core.service.external.user.response.FollowActivityDTO;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FollowActivityResponse implements ActivityResponse{
    private Long followerId;
    private String followerName;
    private Long followeeId;
    private String followeeName;
    private LocalDateTime createdAt;

    public FollowActivityResponse(FollowActivityDTO followActivityDTO) {
        this.followerId = followActivityDTO.getFollowerId();
        this.followerName = followActivityDTO.getFollowerName();
        this.followeeId = followActivityDTO.getFolloweeId();
        this.followeeName = followActivityDTO.getFolloweeName();
        this.createdAt = followActivityDTO.getCreatedAt();
    }
}
