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

@FeignClient(name = "news-feed")
public interface NewsFeedFeignClient {

    @PostMapping("/post/activity/post")
    ResponseEntity<List<PostActivityDTO>> getPostActivities(@RequestBody ActivityRequest activityRequest);

    @PostMapping("/activity/like")
    ResponseEntity<List<PostLikeActivityDTO>> getPostLikeActivities(@RequestBody ActivityRequest activityRequest);

    @PostMapping("/activity/comment")
    ResponseEntity<List<CommentActivityDTO>> getCommentActivities(@RequestBody ActivityRequest activityRequest);

    @PostMapping("/activity/like")
    ResponseEntity<List<CommentLikeActivityDTO>> getCommentLikeActivities(@RequestBody ActivityRequest activityRequest);
}
