package hg.userservice.core.service;

import com.example.commonproject.exception.BadRequestException;
import hg.userservice.core.entity.FollowEntity;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.FollowRepository;
import hg.userservice.core.repository.UserRepository;
import hg.userservice.core.repository.dto.FollowActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hg.userservice.core.entity.FollowEntity.*;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void handleFollow(Long followerId, Long followeeId, boolean follow) {
        UserEntity followerUser = getUserByUserId(followerId);
        UserEntity followeeUser = getUserByUserId(followeeId);

        FollowEntity followEntity = getFollowEntity(followerId, followeeId);

        if (followEntity != null) {
            followEntity.makeDelete(!follow);
        } else if(follow){
            followRepository.save(create(followerUser, followeeUser));
        }

        // TODO : change event 처리 필요 (kafka? or feign?)
    }

    @Transactional(readOnly = true)
    public List<Long> getFollowerList(Long followerId) {
        return followRepository.findFolloweeIdList(followerId);
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
