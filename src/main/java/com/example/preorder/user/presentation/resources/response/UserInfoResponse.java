package com.example.preorder.user.presentation.resources.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponse {
    private String username;
    private String profileImage;
    private String description;

    public UserInfoResponse(String username, String profileImage, String description) {
        this.username = username;
        this.profileImage = profileImage;
        this.description = description;
    }
}
