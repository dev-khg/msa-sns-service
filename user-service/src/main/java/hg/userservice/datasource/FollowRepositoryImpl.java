package hg.userservice.datasource;

import hg.userservice.core.entity.FollowEntity;
import hg.userservice.core.repository.FollowRepository;
import hg.userservice.core.repository.dto.FollowActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository {
    private final FollowJpaRepository followJpaRepository;

    @Override
    public FollowEntity save(FollowEntity followEntity) {
        return followJpaRepository.save(followEntity);
    }

    @Override
    public Optional<FollowEntity> findByFollowerIdAndFolloweeId(Long followerId, Long followeeID) {
        return followJpaRepository.findByFollowerIdAndFolloweeId(followerId, followeeID);
    }

    @Override
    public List<Long> findFolloweeIdList(Long followerId) {
        return followJpaRepository.findFolloweeIdListByFollowerId(followerId);
    }

    @Override
    public List<FollowActivityDTO> findFollowActivity(List<Long> IdList) {
        return followJpaRepository.findFollowActivities(IdList);
    }
}
