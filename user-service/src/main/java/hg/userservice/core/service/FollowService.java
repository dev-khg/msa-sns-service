package hg.userservice.core.service;

import com.example.commonproject.exception.BadRequestException;
import hg.userservice.core.entity.FollowEntity;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.FollowRepository;
import hg.userservice.core.repository.UserRepository;
import hg.userservice.core.repository.dto.FollowActivityDTO;
import hg.userservice.core.service.external.ActivityFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.commonproject.activity.ActivityEvent.create;
import static com.example.commonproject.activity.ActivityType.FOLLOW;
import static com.example.commonproject.activity.ActivityType.UNFOLLOW;
import static hg.userservice.core.entity.FollowEntity.*;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ActivityFeignClient activityFeignClient;

    @Transactional
    public void handleFollow(Long followerId, Long followeeId, boolean follow) {
        UserEntity followerUser = getUserByUserId(followerId);
        UserEntity followeeUser = getUserByUserId(followeeId);

        FollowEntity followEntity = getFollowEntity(followerId, followeeId);

        if (followEntity != null) {
            followEntity.makeDelete(!follow);
            activityFeignClient.handleEvent(create(UNFOLLOW, followerId, followEntity.getId()));
        } else if(follow){
            followRepository.save(FollowEntity.create(followerUser, followeeUser));
            activityFeignClient.handleEvent(create(FOLLOW, followerId, followEntity.getId()));
        }
    }

    @Transactional(readOnly = true)
    public List<Long> getFollowerList(Long followerId) {
        List<Long> followeeIdList = followRepository.findFolloweeIdList(followerId);
        return followeeIdList;
    }

    @Transactional(readOnly = true)
    public List<FollowActivityDTO> getFollowActivities(List<Long> targetIdList) {
        return followRepository.findFollowActivity(targetIdList);
    }

    private UserEntity getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("Not Found User")
        );
    }

    private FollowEntity getFollowEntity(Long followerId, Long followeeId) {
        return followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId).orElse(null);
    }
}
