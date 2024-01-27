package com.example.preorder.post.presentation;

import com.example.preorder.global.security.annotation.AuthorizationRequired;
import com.example.preorder.global.security.annotation.CurrentUser;
import com.example.preorder.post.core.service.PostService;
import com.example.preorder.post.presentation.request.PostCreateRequest;
import com.example.preorder.post.presentation.request.PostGetRequest;
import com.example.preorder.post.presentation.response.PostInfoResponse;
import com.example.preorder.user.core.entity.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping
    @AuthorizationRequired
    public ResponseEntity<Long> enrollPost(@CurrentUser UserEntity userEntity,
                                           @Valid @RequestBody PostCreateRequest postCreateRequest) {
        return ok(postService.enrollPost(userEntity.getId(), postCreateRequest.getContent()));
    }

    @PostMapping("/all")
    public ResponseEntity<List<PostInfoResponse>> findAll(@RequestBody PostGetRequest postGetRequest) {
        return ok(postService.getPostByIds(postGetRequest.getPostIdList()));
    }
}
