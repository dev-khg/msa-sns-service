package hg.userservice.datasource;

import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.UserRepository;
import hg.userservice.core.repository.dto.UserNameInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public boolean existsEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return userJpaRepository.save(userEntity);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public List<UserNameInfoDTO> findUsername(List<Long> userIdList) {
        return userJpaRepository.findActivitiesDTO(userIdList);
    }
}
