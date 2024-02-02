package hg.userservice.presentation;

import com.example.commonproject.response.ApiResponse;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.dto.UserNameInfoDTO;
import hg.userservice.core.service.UserService;
import hg.userservice.infrastructure.security.annotation.AuthorizationRequired;
import hg.userservice.infrastructure.security.annotation.CurrentUser;
import hg.userservice.presentation.request.EditInfoRequest;
import hg.userservice.presentation.request.EditPasswordRequest;
import hg.userservice.presentation.request.ActivityRequest;
import hg.userservice.presentation.response.UserInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.commonproject.response.ApiResponse.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(@PathVariable Long userId) {
        return ok(success(userService.getUserInfo(userId)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(@CurrentUser Long userId) {
        return ok(success(userService.getUserInfo(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> changeMyInfo(
            @CurrentUser Long userId, @Valid @RequestBody EditInfoRequest request) {
        userService.editUserInfo(userId, request);

        return noContent().build();
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @CurrentUser Long userId, @Valid @RequestBody EditPasswordRequest request) {
        userService.changePassword(userId, request);

        return noContent().build();
    }

    @PatchMapping(value = "/me/profile", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> changeProfile(
            @CurrentUser Long userId, @RequestPart(name = "file") MultipartFile file) {
        userService.changeProfile(userId, file);

        return noContent().build();
    }

    @PostMapping("/name")
    public ResponseEntity<List<UserNameInfoDTO>> getUsernames(@RequestBody ActivityRequest request) {
        return ok(userService.getActivities(request.getTargetIdList()));
    }

}
