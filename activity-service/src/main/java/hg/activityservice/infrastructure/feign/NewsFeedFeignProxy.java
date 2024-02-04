package hg.activityservice.infrastructure.feign;

import com.example.commonproject.response.ApiResponse;
import hg.activityservice.core.service.external.newfeed.NewsFeedFeignClient;
import hg.activityservice.core.service.external.newfeed.request.ActivityRequest;
import hg.activityservice.core.service.external.newfeed.response.CommentActivityDTO;
import hg.activityservice.core.service.external.newfeed.response.CommentLikeActivityDTO;
import hg.activityservice.core.service.external.newfeed.response.PostActivityDTO;
import hg.activityservice.core.service.external.newfeed.response.PostLikeActivityDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.security.auth.kerberos.KerberosTicket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsFeedFeignProxy {
    private final NewsFeedFeignClient newsFeedFeignClient;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private CircuitBreaker circuitbreaker;

    @PostConstruct
    void init() {
        this.circuitbreaker = circuitBreakerFactory.create("newsfeed-circuit");
    }

    public ResponseEntity<List<PostActivityDTO>> getPostActivities(ActivityRequest activityRequest) {
        return circuitbreaker.run(() -> newsFeedFeignClient.getPostActivities(activityRequest),
                throwable -> ResponseEntity.internalServerError().body(new ArrayList<>())
        );
    }

    public ResponseEntity<List<PostLikeActivityDTO>> getPostLikeActivities(ActivityRequest activityRequest) {
        return circuitbreaker.run(() -> newsFeedFeignClient.getPostLikeActivities(activityRequest),
                throwable -> ResponseEntity.internalServerError().body(new ArrayList<>())
        );
    }

    public ResponseEntity<ApiResponse<List<CommentActivityDTO>>> getCommentActivities(ActivityRequest activityRequest) {
        return circuitbreaker.run(() -> newsFeedFeignClient.getCommentActivities(activityRequest),
                throwable -> ResponseEntity.internalServerError().body(ApiResponse.success(new ArrayList<>()))
        );
    }

    public ResponseEntity<ApiResponse<List<CommentLikeActivityDTO>>> getCommentLikeActivities(ActivityRequest activityRequest) {
        return circuitbreaker.run(() -> newsFeedFeignClient.getCommentLikeActivities(activityRequest),
                throwable -> ResponseEntity.internalServerError().body(ApiResponse.success(new ArrayList<>()))
        );
    }

}
