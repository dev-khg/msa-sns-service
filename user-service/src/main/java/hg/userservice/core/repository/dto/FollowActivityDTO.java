package hg.userservice.core.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowActivityDTO {
    private Long followerId;
    private String followerName;
    private Long followeeId;
    private String followeeName;
    private LocalDateTime createdAt;
}
