package com.example.newsfeedservice.presentation;

import com.example.commonproject.response.ApiResponse;
import com.example.newsfeedservice.common.annotation.CurrentUser;
import com.example.newsfeedservice.core.repository.dto.PostActivityDTO;
import com.example.newsfeedservice.core.repository.dto.PostLikeActivityDTO;
import com.example.newsfeedservice.core.service.PostLikeService;
import com.example.newsfeedservice.core.service.PostService;
import com.example.newsfeedservice.presentation.request.ActivityRequest;
import com.example.newsfeedservice.presentation.request.PostCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.commonproject.response.ApiResponse.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final PostLikeService postLikeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> enrollPost(@CurrentUser Long userId,
                                                        @Valid @RequestBody PostCreateRequest postCreateRequest) {
        return ok(success(postService.enrollPost(userId, postCreateRequest.getContent())));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@CurrentUser Long userId, @PathVariable Long postId) {
        postLikeService.handlePostLike(userId, postId, true);

        return noContent().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(@CurrentUser Long userId, @PathVariable Long postId) {
        postLikeService.handlePostLike(userId, postId, false);

        return noContent().build();
    }

    @PostMapping("/activity/post")
    public ResponseEntity<List<PostActivityDTO>> getPostActivities(
            @RequestBody ActivityRequest activityRequest) {
        return ok(postService.getActivities(activityRequest.getTargetIdList()));
    }

    @PostMapping("/activity/like")
    public ResponseEntity<List<PostLikeActivityDTO>> getPostLikeActivities(
            @RequestBody ActivityRequest activityRequest) {
        return ok(postLikeService.getActivities(activityRequest.getTargetIdList()));
    }
}
