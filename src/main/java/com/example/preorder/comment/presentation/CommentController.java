package com.example.preorder.comment.presentation;

import com.example.preorder.comment.core.entity.CommentLikeStatus;
import com.example.preorder.comment.core.service.CommentLikeService;
import com.example.preorder.comment.core.service.CommentService;
import com.example.preorder.comment.presentation.request.CommentCreateRequest;
import com.example.preorder.comment.presentation.request.CommentGetRequest;
import com.example.preorder.comment.presentation.request.CommentLikeGetRequest;
import com.example.preorder.comment.presentation.response.CommentActivityResponse;
import com.example.preorder.comment.presentation.response.CommentInfoResponse;
import com.example.preorder.comment.presentation.response.CommentLikeActivityResponse;
import com.example.preorder.common.ApiResponse;
import com.example.preorder.global.security.annotation.AuthorizationRequired;
import com.example.preorder.global.security.annotation.CurrentUser;
import com.example.preorder.user.core.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.preorder.comment.core.entity.CommentLikeStatus.*;
import static com.example.preorder.common.ApiResponse.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    @PostMapping("/{postId}")
    @AuthorizationRequired
    public ResponseEntity<ApiResponse<Long>> enrollComment(
            @CurrentUser UserEntity user,
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest createRequest) {
        return ok(success(
                commentService.enrollComment(user.getId(), postId, createRequest.getContent())
        ));
    }

    @PostMapping("/{commentId}/like")
    @AuthorizationRequired
    public ResponseEntity<Void> likeComment(@CurrentUser UserEntity userEntity, @PathVariable Long commentId) {
        commentLikeService.handleCommentLike(userEntity.getId(), commentId, ACTIVATE);

        return noContent().build();
    }

    @DeleteMapping("/{commentId}/like")
    @AuthorizationRequired
    public ResponseEntity<Void> unLikeComment(@CurrentUser UserEntity userEntity, @PathVariable Long commentId) {
        commentLikeService.handleCommentLike(userEntity.getId(), commentId, DEACTIVATE);

        return ok().build();
    }
}
