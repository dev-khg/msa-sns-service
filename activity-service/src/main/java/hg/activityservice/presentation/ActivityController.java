package hg.activityservice.presentation;

import com.example.commonproject.activity.ActivityEvent;
import com.example.commonproject.response.ApiResponse;
import hg.activityservice.common.annotation.CurrentUser;
import hg.activityservice.core.service.ActivityFacade;
import hg.activityservice.presentation.response.ActivityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.commonproject.response.ApiResponse.success;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityFacade activityFacade;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> getActivities(
            @CurrentUser Long userId,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size) {
        Pageable pageable = getPagingInfo(page, size);

        return ok(success(activityFacade.getFeeds(userId, pageable)));
    }

    @PostMapping
    public void handleEvent(@RequestBody ActivityEvent event) {
        activityFacade.handleActivityEvent(event);
    }

    private Pageable getPagingInfo(Integer page, Integer size) {
        return of(page - 1, size, Sort.Direction.DESC, "createdAt");
    }
}
