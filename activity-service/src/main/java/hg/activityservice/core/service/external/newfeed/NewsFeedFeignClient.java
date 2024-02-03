package hg.activityservice.core.service.external.newfeed;

import com.example.commonproject.response.ApiResponse;
import hg.activityservice.core.service.external.newfeed.request.ActivityRequest;
import hg.activityservice.core.service.external.newfeed.response.CommentActivityDTO;
import hg.activityservice.core.service.external.newfeed.response.CommentLikeActivityDTO;
import hg.activityservice.core.service.external.newfeed.response.PostActivityDTO;
import hg.activityservice.core.service.external.newfeed.response.PostLikeActivityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "NEWSFEED-SERVICE")
public interface NewsFeedFeignClient {

    @PostMapping("/post/activity")
    ResponseEntity<List<PostActivityDTO>> getPostActivities(@RequestBody ActivityRequest activityRequest);

    @PostMapping("/post/like/activity")
    ResponseEntity<List<PostLikeActivityDTO>> getPostLikeActivities(@RequestBody ActivityRequest activityRequest);

    @PostMapping("/comment/activity")
    ResponseEntity<ApiResponse<List<CommentActivityDTO>>> getCommentActivities(@RequestBody ActivityRequest activityRequest);

    @PostMapping("/comment/like/activity")
    ResponseEntity<ApiResponse<List<CommentLikeActivityDTO>>> getCommentLikeActivities(@RequestBody ActivityRequest activityRequest);
}
