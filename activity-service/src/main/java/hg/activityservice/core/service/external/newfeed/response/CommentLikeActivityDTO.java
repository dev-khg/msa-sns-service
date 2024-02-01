package hg.activityservice.core.service.external.newfeed.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommentLikeActivityDTO {
    private Long commentId;
    private Long commentUserId;
    private Long userId;
    private LocalDateTime createdAt;
}
