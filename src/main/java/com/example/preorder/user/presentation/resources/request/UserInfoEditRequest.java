package com.example.preorder.user.presentation.resources.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoEditRequest {
    @NotBlank(message = "postLikeIdList must be not null")
    private String username;

    @NotBlank(message = "postLikeIdList must be not null")
    private String description;
}
