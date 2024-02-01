package hg.userservice.presentation;

import com.example.commonproject.response.ApiResponse;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.dto.FollowActivityDTO;
import hg.userservice.core.service.FollowService;
import hg.userservice.infrastructure.security.annotation.AuthorizationRequired;
import hg.userservice.infrastructure.security.annotation.CurrentUser;
import hg.userservice.presentation.request.ActivityRequest;
import hg.userservice.presentation.request.FollowActivityRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.commonproject.response.ApiResponse.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{followeeId}")
    @AuthorizationRequired
    public ResponseEntity<Void> follow(@CurrentUser UserEntity userEntity, @PathVariable Long followeeId) {
        followService.handleFollow(userEntity.getId(), followeeId, true);

        return noContent().build();
    }

    @DeleteMapping("/{followeeId}")
    @AuthorizationRequired
    public ResponseEntity<Void> unFollow(@CurrentUser UserEntity userEntity, @PathVariable Long followeeId) {
        followService.handleFollow(userEntity.getId(), followeeId, false);

        return noContent().build();
    }

    @GetMapping("/{followerId}/follower-list")
    public ApiResponse<List<Long>> getFollowerList(@PathVariable Long followerId) {
        return success(followService.getFollowerList(followerId));
    }

    @PostMapping("/activity")
    public ResponseEntity<List<FollowActivityDTO>> getActivities(@RequestBody ActivityRequest request) {
        return ok(followService.getFollowActivities(request.getTargetIdList()));
    }
}
