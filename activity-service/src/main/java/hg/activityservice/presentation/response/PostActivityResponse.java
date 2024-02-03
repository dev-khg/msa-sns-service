package hg.activityservice.presentation.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import hg.activityservice.core.service.external.newfeed.response.PostActivityDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostActivityResponse implements ActivityResponse {
    private Long userId;
    private String username;
    private String content;
    private Long postId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public PostActivityResponse(PostActivityDTO postActivityDTO, String username) {
        this.userId = postActivityDTO.getUserId();
        this.username = username;
        this.content = postActivityDTO.getContent();
        this.postId = postActivityDTO.getPostId();
        this.createdAt = postActivityDTO.getCreatedAt();
    }
}
