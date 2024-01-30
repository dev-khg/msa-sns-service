package hg.userservice.core.repository;

import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.dto.UserNameInfoDTO;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findById(Long id);

    boolean existsEmail(String email);

    boolean existsUsername(String username);

    UserEntity save(UserEntity userEntity);

    Optional<UserEntity> findByEmail(String email);

    List<UserNameInfoDTO> findUsername(List<Long> userIdList);
}
