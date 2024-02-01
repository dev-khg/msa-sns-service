package hg.activityservice.presentation.response;

import hg.activityservice.core.service.external.newfeed.response.PostLikeActivityDTO;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostLikeActivityResponse implements ActivityResponse {
    private Long userId;
    private String username;
    private Long postUserId;
    private String postUsername;
    private LocalDateTime createdAt;

    public PostLikeActivityResponse(
            PostLikeActivityDTO postLikeActivityDTO, String username, String postUsername) {
        this.userId = postLikeActivityDTO.getUserId();
        this.username = username;
        this.postUserId = postLikeActivityDTO.getPostUserId();
        this.postUsername = postUsername;
        this.createdAt = postLikeActivityDTO.getCreatedAt();
    }
}
