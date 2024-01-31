package hg.userservice.core.repository;

import hg.userservice.core.entity.FollowEntity;
import hg.userservice.core.repository.dto.FollowActivityDTO;

import java.util.List;
import java.util.Optional;

public interface FollowRepository {
    Optional<FollowEntity> findById(Long id);

    FollowEntity save(FollowEntity followEntity);

    Optional<FollowEntity> findByFollowerIdAndFolloweeId(Long followerId, Long followeeID);

    List<Long> findFolloweeIdList(Long followerId);

    List<FollowActivityDTO> findFollowActivity(List<Long> IdList);
}
