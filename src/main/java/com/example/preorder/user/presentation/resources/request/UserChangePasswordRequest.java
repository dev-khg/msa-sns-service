package com.example.preorder.user.presentation.resources.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserChangePasswordRequest {
    @NotBlank(message = "Current password must be not blank")
    @Size(min = 8, max = 20, message = "Current password length must bigger than 8 and must smaller than 20.")
    private String currentPassword;
    @NotBlank(message = "New password must be not blank")
    @Size(min = 8, max = 20, message = "New password length must bigger than 8 and must smaller than 20.")
    private String newPassword;
}
