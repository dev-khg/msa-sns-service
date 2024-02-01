package hg.activityservice.core.service.external.user;

import com.example.commonproject.response.ApiResponse;
import hg.activityservice.core.service.external.newfeed.request.ActivityRequest;
import hg.activityservice.core.service.external.user.response.FollowActivityDTO;
import hg.activityservice.core.service.external.user.response.UserNameInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @PostMapping("/user/name")
    ResponseEntity<List<UserNameInfoDTO>> getUsernames(@RequestBody ActivityRequest request);

    @GetMapping("/follow/{followerId}/follower-list")
    ResponseEntity<List<Long>> getFollowerList(@PathVariable Long followerId);

    @GetMapping("/follow/activity")
    ResponseEntity<List<FollowActivityDTO>> getFollowActivities(@RequestBody ActivityRequest request);
}
