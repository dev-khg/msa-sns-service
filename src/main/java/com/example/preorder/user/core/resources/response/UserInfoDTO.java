package com.example.preorder.user.core.resources.response;

import lombok.Getter;

@Getter
public class UserInfoDTO {
    private String username;
    private String profileImage;
    private String description;

    public UserInfoDTO(String username, String profileImage, String description) {
        this.username = username;
        this.profileImage = profileImage;
        this.description = description;
    }
}
