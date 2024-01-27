package com.example.preorder.follow.presentation;

import com.example.preorder.follow.core.service.FollowService;
import com.example.preorder.global.security.annotation.AuthorizationRequired;
import com.example.preorder.global.security.annotation.CurrentUser;
import com.example.preorder.user.core.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.preorder.follow.core.entity.FollowStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{userId}")
    @AuthorizationRequired
    public void follow(@CurrentUser UserEntity userEntity, @PathVariable Long userId) {
        followService.handleFollow(userEntity.getId(), userId, FOLLOWING);
    }

    @DeleteMapping("/{userId}")
    @AuthorizationRequired
    public void unFollow(@CurrentUser UserEntity userEntity, @PathVariable Long userId) {
        followService.handleFollow(userEntity.getId(), userId, UNFOLLOWING);
    }
}
