package hg.userservice.datasource;

import hg.userservice.core.entity.FollowEntity;
import hg.userservice.core.repository.dto.FollowActivityDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowJpaRepository extends JpaRepository<FollowEntity, Long> {

    Optional<FollowEntity> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    @Query("select f.followee.id from FollowEntity f " +
            "where f.follower.id = :followerId and f.deletedAt is null")
    List<Long> findFolloweeIdListByFollowerId(@Param("followerId") Long followerId);

    @Query("select new hg.userservice.core.repository.dto.FollowActivityDTO" +
            "(u2.id, u2.username, u1.id, u1.username, f.updatedAt) " +
            "from FollowEntity f " +
            "left join UserEntity u1 on u1.id = f.followee.id " +
            "left join UserEntity u2 on u2.id = f.follower.id " +
            "where f.id in (:id_list) and f.deletedAt is null"
    )
    List<FollowActivityDTO> findFollowActivities(@Param("id_list") List<Long> idList);
}
