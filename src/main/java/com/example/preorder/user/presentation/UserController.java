package com.example.preorder.user.presentation;

import com.example.preorder.common.ApiResponse;
import com.example.preorder.common.utils.HttpServletUtils;
import com.example.preorder.global.security.annotation.AuthorizationRequired;
import com.example.preorder.global.security.annotation.CurrentUser;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.service.UserService;
import com.example.preorder.user.presentation.resources.request.UserChangePasswordRequest;
import com.example.preorder.user.presentation.resources.request.UserInfoEditRequest;
import com.example.preorder.user.presentation.resources.request.UserSignUpRequest;
import com.example.preorder.user.presentation.resources.response.TokenResponse;
import com.example.preorder.user.presentation.resources.response.UserInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.preorder.common.ApiResponse.*;
import static com.example.preorder.common.utils.HttpServletUtils.CookieType.*;
import static com.example.preorder.common.utils.HttpServletUtils.HeaderType.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final HttpServletUtils httpServletUtils;

    @PostMapping
    public ResponseEntity<Void> signUp(@Valid @RequestBody UserSignUpRequest userSignUp) {

        TokenResponse tokenResponse = userService.signUp(userSignUp);
        applyToken(tokenResponse);

        return noContent().build();
    }

    @PostMapping("/token")
    public ResponseEntity<Void> reissueToken(
            @CookieValue(value = "RefreshToken", required = false) String refreshToken
    ) {
        TokenResponse tokenResponse = userService.reissueToken(refreshToken);
        applyToken(tokenResponse);

        return noContent().build();
    }

    @PatchMapping("/password")
    @AuthorizationRequired
    public ResponseEntity<Void> changePassword(
            @CurrentUser UserEntity currentUser, @Valid @RequestBody UserChangePasswordRequest passwordRequest
    ) {
        userService.editPassword(currentUser, passwordRequest);

        return noContent().build();
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(@PathVariable Long userId) {
        return ok(success(userService.getUserInfo(userId)));
    }

    @GetMapping("/info")
    @AuthorizationRequired
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(@CurrentUser UserEntity userEntity) {
        return ok(success(userService.getUserInfo(userEntity.getId())));
    }

    @AuthorizationRequired
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> changeMyInfo(
            @CurrentUser UserEntity userEntity,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @Valid @RequestPart(value = "user_info_edit", required = false) UserInfoEditRequest userEdit) {
        userService.editInfo(userEntity, userEdit, file);
        return noContent().build();
    }

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@RequestPart("email") String email) {
        userService.sendAuthCode(email);
        return noContent().build();
    }

    @DeleteMapping
    @AuthorizationRequired
    public ResponseEntity<Void> logout(
            @RequestHeader(name = AUTHORIZATION, required = false) String accessToken,
            @CookieValue(value = "RefreshToken", required = false) String refreshToken
    ) {
        userService.logout(accessToken, refreshToken);
        return noContent().build();
    }

    private void applyToken(TokenResponse tokenResponse) {
        httpServletUtils.putHeader(ACCESS_TOKEN, tokenResponse.getAccessToken());
        httpServletUtils.addCookie(REFRESH_TOKEN, tokenResponse.getRefreshToken());
    }
}
