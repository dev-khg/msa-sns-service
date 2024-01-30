package hg.userservice.datasource;

import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.dto.UserNameInfoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    @Query("select new hg.userservice.core.repository.dto.UserNameInfoDTO(u.id, u.username) " +
            "from UserEntity u " +
            "where u.id in (:user_id_list) " +
            "order by u.id asc")
    List<UserNameInfoDTO> findActivitiesDTO(@Param("user_id_list") List<Long> userIdList);
}
