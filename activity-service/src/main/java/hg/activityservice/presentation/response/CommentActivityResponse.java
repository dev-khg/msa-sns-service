package hg.activityservice.presentation.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import hg.activityservice.core.service.external.newfeed.response.CommentActivityDTO;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentActivityResponse implements ActivityResponse{
    private Long commentId;
    private Long userId;
    private String username;
    private Long postId;
    private Long postUserId;
    private String postUsername;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public CommentActivityResponse(
            CommentActivityDTO commentActivityDTO, String username, String postUsername) {
        this.commentId = commentActivityDTO.getCommentId();
        this.userId = commentActivityDTO.getUserId();
        this.username = username;
        this.postId = commentActivityDTO.getPostId();
        this.postUserId = commentActivityDTO.getPostUserId();
        this.postUsername = postUsername;
        this.createdAt = commentActivityDTO.getCreatedAt();
    }
}
