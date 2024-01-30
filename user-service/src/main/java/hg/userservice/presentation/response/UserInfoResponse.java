package hg.userservice.presentation.response;

import hg.userservice.core.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String username;
    private String profileImage;
    private String description;

    public UserInfoResponse(UserEntity userEntity) {
        this.userId = userEntity.getId();
        this.username = userEntity.getUsername();
        this.profileImage = userEntity.getProfileImage();
        this.description = userEntity.getDescription();
    }
}
