package com.example.preorder.activity.presentation;

import com.example.preorder.activity.core.service.ActivityService;
import com.example.preorder.common.ApiResponse;
import com.example.preorder.common.activity.ActivityResponse;
import com.example.preorder.global.security.annotation.AuthorizationRequired;
import com.example.preorder.global.security.annotation.CurrentUser;
import com.example.preorder.user.core.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.preorder.common.ApiResponse.*;
import static org.springframework.data.domain.PageRequest.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping
    @AuthorizationRequired
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> getActivities(
            @CurrentUser UserEntity user,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size) {
        Pageable pageable = getPagingInfo(page, size);

        return ok(success(activityService.getActivities(user.getId(), pageable)));
    }

    private Pageable getPagingInfo(Integer page, Integer size) {
        return of(page - 1, size, Sort.Direction.DESC, "createdAt");
    }
}
