package hg.activityservice.core.service.external.newfeed.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeActivityDTO {
    private Long userId;
    private Long postUserId;
    private Long postId;
    private LocalDateTime createdAt;
}
