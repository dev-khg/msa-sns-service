package com.example.preorder.user.core.service;

import com.example.preorder.user.presentation.resources.request.UserChangePasswordRequest;
import com.example.preorder.user.presentation.resources.request.UserInfoEditRequest;
import com.example.preorder.user.presentation.resources.response.TokenResponse;
import com.example.preorder.user.presentation.resources.request.UserSignUpRequest;
import com.example.preorder.user.presentation.resources.response.UserInfoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    TokenResponse signUp(UserSignUpRequest signUpDTO, MultipartFile file);

    TokenResponse reissueToken(String refreshToken);

    UserInfoResponse getUserInfo(Long userId);

    void sendAuthCode(String email);

    void logout(String accessToken, String refreshToken);

    void editPassword(UserChangePasswordRequest changePasswordDTO);

    void editInfo(UserInfoEditRequest infoEditDTO);
}
