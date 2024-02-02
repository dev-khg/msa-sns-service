package com.example.newsfeedservice.presentation;

import com.example.commonproject.response.ApiResponse;
import com.example.newsfeedservice.common.annotation.CurrentUser;
import com.example.newsfeedservice.core.repository.dto.CommentActivityDTO;
import com.example.newsfeedservice.core.repository.dto.CommentLikeActivityDTO;
import com.example.newsfeedservice.core.repository.dto.PostLikeActivityDTO;
import com.example.newsfeedservice.core.service.CommentLikeService;
import com.example.newsfeedservice.core.service.CommentService;
import com.example.newsfeedservice.presentation.request.ActivityRequest;
import com.example.newsfeedservice.presentation.request.CommentCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.commonproject.response.ApiResponse.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<Long>> enrollComment(@CurrentUser Long userId,
                                                           @PathVariable Long postId,
                                                           @Valid @RequestBody CommentCreateRequest request) {
        return ok(success(commentService.enrollComment(userId, postId, request.getContent())));
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> likeComment(@CurrentUser Long userId, @PathVariable Long commentId) {
        commentLikeService.handleCommentLike(userId, commentId, true);
        return noContent().build();
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@CurrentUser Long userId, @PathVariable Long commentId) {
        commentLikeService.handleCommentLike(userId, commentId, false);
        return noContent().build();
    }

    @PostMapping("/activity")
    public ResponseEntity<ApiResponse<List<CommentActivityDTO>>> getCommentActivities(
            @RequestBody ActivityRequest activityRequest) {
        return ok(success(commentService.getCommentActivities(activityRequest.getTargetIdList())));
    }

    @PostMapping("/like/activity")
    public ResponseEntity<ApiResponse<List<CommentLikeActivityDTO>>> getCommentLikeActivities(
            @RequestBody ActivityRequest activityRequest) {
        return ok(success(commentLikeService.getActivities(activityRequest.getTargetIdList())));
    }
}
