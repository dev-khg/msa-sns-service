package com.example.preorder.user.core.service;

import com.example.preorder.user.core.resources.request.UserChangePasswordDTO;
import com.example.preorder.user.core.resources.request.UserInfoEditDTO;
import com.example.preorder.user.core.resources.response.TokenDTO;
import com.example.preorder.user.core.resources.request.UserLoginDTO;
import com.example.preorder.user.core.resources.request.UserSignUpDTO;
import com.example.preorder.user.core.resources.response.UserInfoDTO;

public interface UserService {

    TokenDTO signUp(UserSignUpDTO signUpDTO);

    TokenDTO login(UserLoginDTO loginDTO);

    TokenDTO reissueToken();

    UserInfoDTO getUserInfo(Long userId);

    UserInfoDTO getMyInfo();

    void sendEmail(String email);

    void logout();

    void editPassword(UserChangePasswordDTO changePasswordDTO);

    void editInfo(UserInfoEditDTO infoEditDTO);
}