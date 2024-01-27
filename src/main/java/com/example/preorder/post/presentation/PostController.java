package com.example.preorder.post.presentation;

import com.example.preorder.global.security.annotation.AuthorizationRequired;
import com.example.preorder.global.security.annotation.CurrentUser;
import com.example.preorder.post.core.service.CommentLikeService;
import com.example.preorder.post.core.service.CommentPublisher;
import com.example.preorder.post.core.service.PostLikePublisher;
import com.example.preorder.post.core.service.PostPublisher;
import com.example.preorder.post.core.vo.CommentLikeStatus;
import com.example.preorder.post.core.vo.PostLikeStatus;
import com.example.preorder.post.presentation.request.CommentCreateRequest;
import com.example.preorder.post.presentation.request.PostCreateRequest;
import com.example.preorder.post.presentation.response.CommentResponse;
import com.example.preorder.post.presentation.response.PostInfoResponse;
import com.example.preorder.user.core.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class PostController {
    private final PostPublisher postService;
    private final PostLikePublisher postLikeService;
    private final CommentPublisher commentService;
    private final CommentLikeService commentLikeService;

    @PostMapping
    @AuthorizationRequired
    public ResponseEntity<Long> createPost(@CurrentUser UserEntity userEntity,
                                           @RequestBody PostCreateRequest postCreateRequest) {
        Long postId = postService.createPost(
                userEntity.getId(),
                userEntity.getUsername(),
                postCreateRequest
        );

        return new ResponseEntity<>(postId, OK);
    }

    @DeleteMapping("/{postId}")
    @AuthorizationRequired
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @CurrentUser UserEntity userEntity) {
        postService.deletePost(userEntity.getId(), postId);

        return new ResponseEntity<>(OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostInfoResponse> getPost(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.getPost(postId), OK);
    }

    @GetMapping("/{postId}/like")
    @AuthorizationRequired
    public ResponseEntity<Void> getPost(@PathVariable Long postId, @CurrentUser UserEntity userEntity) {
        postLikeService.createPostLike(
                userEntity.getId(), postId, userEntity.getUsername(), PostLikeStatus.ACTIVATE
        );

        return new ResponseEntity<>(OK);
    }

    @PostMapping("/{postId}/comment")
    @AuthorizationRequired
    public ResponseEntity<Long> createComment(@PathVariable Long postId,
                                              @CurrentUser UserEntity userEntity,
                                              @RequestBody CommentCreateRequest commentCreateRequest) {
        return ResponseEntity.ok(commentService.enrollComment(
                userEntity.getId(), userEntity.getUsername(), postId, commentCreateRequest
        ));
    }

    @GetMapping("/feed/{postId}/comment")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @PostMapping("/feed/{commentId}/comment/like")
    @AuthorizationRequired
    public ResponseEntity<Void> likeComment(@CurrentUser UserEntity userEntity, @PathVariable Long commentId) {
        commentLikeService.handleCommentLike(
                userEntity.getId(),
                userEntity.getUsername(),
                commentId,
                CommentLikeStatus.ACTIVATE
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/feed/{commentId}/comment/like")
    @AuthorizationRequired
    public ResponseEntity<Void> disLikeComment(@CurrentUser UserEntity userEntity, @PathVariable Long commentId) {
        commentLikeService.handleCommentLike(
                userEntity.getId(),
                userEntity.getUsername(),
                commentId,
                CommentLikeStatus.DEACTIVATE
        );

        return ResponseEntity.ok().build();
    }
}
