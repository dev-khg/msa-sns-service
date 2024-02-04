package hg.activityservice.presentation.response;

import com.example.commonproject.activity.ActivityType;
import com.fasterxml.jackson.annotation.JsonFormat;
import hg.activityservice.core.service.external.newfeed.response.CommentLikeActivityDTO;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentLikeActivityResponse extends ActivityBase{
    private Long commentId;
    private Long commentUserId;
    private Long userId;
    private String username;
    private String commentUsername;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public CommentLikeActivityResponse(
            CommentLikeActivityDTO commentLikeActivityDTO, String username, String commentUsername) {
        super(ActivityType.COMMENT_LIKE);
        this.commentId = commentLikeActivityDTO.getCommentId();
        this.commentUserId = commentLikeActivityDTO.getCommentUserId();
        this.userId = commentLikeActivityDTO.getUserId();
        this.username = username;
        this.commentUsername = commentUsername;
        this.createdAt = commentLikeActivityDTO.getCreatedAt();
    }
}
