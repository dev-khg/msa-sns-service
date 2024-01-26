package com.example.preorder.follow.presentation;

import com.example.preorder.follow.core.service.FollowService;
import com.example.preorder.follow.core.vo.FollowStatus;
import com.example.preorder.global.security.annotation.AuthorizationRequired;
import com.example.preorder.global.security.annotation.CurrentUser;
import com.example.preorder.user.core.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.preorder.follow.core.vo.FollowStatus.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{targetId}")
    @AuthorizationRequired
    public ResponseEntity<Void> follow(@CurrentUser UserEntity userEntity, @PathVariable Long targetId) {
        followService.handleFollow(userEntity.getId(), targetId, ACTIVE);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @DeleteMapping("/{targetId}")
    @AuthorizationRequired
    public ResponseEntity<Void> unFollow(@CurrentUser UserEntity userEntity, @PathVariable Long targetId) {
        followService.handleFollow(userEntity.getId(), targetId, DEACTIVATE);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
