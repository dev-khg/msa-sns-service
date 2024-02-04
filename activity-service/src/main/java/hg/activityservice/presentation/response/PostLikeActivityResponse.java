package hg.activityservice.presentation.response;

import com.example.commonproject.activity.ActivityType;
import com.fasterxml.jackson.annotation.JsonFormat;
import hg.activityservice.core.service.external.newfeed.response.PostLikeActivityDTO;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostLikeActivityResponse extends ActivityBase {
    private Long userId;
    private String username;
    private Long postUserId;
    private String postUsername;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public PostLikeActivityResponse(
            PostLikeActivityDTO postLikeActivityDTO, String username, String postUsername) {
        super(ActivityType.POST_LIKE);
        this.userId = postLikeActivityDTO.getUserId();
        this.username = username;
        this.postUserId = postLikeActivityDTO.getPostUserId();
        this.postUsername = postUsername;
        this.createdAt = postLikeActivityDTO.getCreatedAt();
    }
}
