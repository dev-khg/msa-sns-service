package hg.activityservice.core.service.external.newfeed.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentActivityDTO {
    private Long commentId;
    private Long userId;
    private Long postId;
    private Long postUserId;
    private LocalDateTime createdAt;
}
