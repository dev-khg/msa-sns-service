package hg.userservice.core.service;

import hg.userservice.core.entity.FollowEntity;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.UserRepository;
import hg.userservice.core.repository.dto.FollowActivityDTO;
import hg.userservice.datasource.FollowRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static hg.userservice.core.entity.UserEntity.create;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FollowServiceTest {

    @Autowired
    FollowRepositoryImpl followRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowService followService;

    @PersistenceContext
    EntityManager em;

    List<UserEntity> userEntities;

    @BeforeEach
    void beforeEach() {
        userEntities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserEntity userEntity = createUserEntity();
            userEntities.add(userEntity);
        }
        flushAndClearPersistence();
    }

    @Test
    @DisplayName("팔로우 내역이 없는 유저를 팔로우하면 데이터베이스에 저장되어야 한다.")
    void follow_not_exists_history() {
        // given
        UserEntity followerUser = userEntities.get(0);
        UserEntity followeeUser = userEntities.get(1);

        // when
        followService.handleFollow(followerUser.getId(), followeeUser.getId(), true);
        flushAndClearPersistence();

        // then
        Optional<FollowEntity> byFollowerIdAndFolloweeId =
                followRepository.findByFollowerIdAndFolloweeId(followerUser.getId(), followeeUser.getId());
        assertTrue(byFollowerIdAndFolloweeId.isPresent());
    }

    @Test
    @DisplayName("팔로우 내역이 있는 유저를 팔로우하면 데이터베이스에 중복 저장되면 안된다.")
    void follow_exists_history_do_not_save() {
        // given
        UserEntity followerUser = userEntities.get(0);
        UserEntity followeeUser = userEntities.get(1);

        // when
        followService.handleFollow(followerUser.getId(), followeeUser.getId(), true);
        flushAndClearPersistence();

        // then
        followService.handleFollow(followerUser.getId(), followeeUser.getId(), true);
    }

    @Test
    @DisplayName("팔로우 내역이 없는 유저를 언팔로우하면 데이터베이스에 저장되면 안된다.")
    void unfollow_not_exists_history_do_not_save() {
        // given
        UserEntity followerUser = userEntities.get(0);
        UserEntity followeeUser = userEntities.get(1);

        // when
        followService.handleFollow(followerUser.getId(), followeeUser.getId(), false);
        flushAndClearPersistence();

        // then
        Optional<FollowEntity> optionalFollow =
                followRepository.findByFollowerIdAndFolloweeId(followerUser.getId(), followeeUser.getId());
        assertTrue(optionalFollow.isEmpty());
    }

    @Test
    @DisplayName("팔로우 내역이 있는 유저를 언팔로우하면 데이터베이스에 정상적으로 반영되어야 한다.")
    void unfollow_exists_history() {
        // given
        UserEntity followerUser = userEntities.get(0);
        UserEntity followeeUser = userEntities.get(1);
        followService.handleFollow(followerUser.getId(), followeeUser.getId(), true);
        flushAndClearPersistence();

        // when
        followService.handleFollow(followerUser.getId(), followeeUser.getId(), false);
        flushAndClearPersistence();

        // then
        FollowEntity optionalFollow =
                followRepository.findByFollowerIdAndFolloweeId(followerUser.getId(), followeeUser.getId())
                        .orElseThrow();
        assertNotNull(optionalFollow.getDeletedAt());
    }

    @Test
    @DisplayName("팔로우 리스트를 불러올 때, 언팔로우 데이터는 불러오면 안된다.")
    void find_follow_list() {
        // given
        List<Long> followeeIdList = new ArrayList<>();
        UserEntity followerEntity = userEntities.get(0);
        for (int i = 1; i < userEntities.size(); i++) {
            FollowEntity followEntity = FollowEntity.create(followerEntity, userEntities.get(i));
            followRepository.save(followEntity);
            flushAndClearPersistence();
            followeeIdList.add(followEntity.getId());
        }

        // when

        // then
        while (!followeeIdList.isEmpty()) {
            List<Long> followeeIds = followService.getFollowerList(followerEntity.getId());
            assertEquals(followeeIds.size(), followeeIdList.size());

            Long removeId = followeeIdList.remove(0);
            FollowEntity followEntity = followRepository.findById(removeId).orElseThrow();
            followEntity.makeDelete(true);
            flushAndClearPersistence();
        }
    }

    @Test
    @DisplayName("팔로우 활동 내역을 불러올 때, 언팔로우 활동 내역은 불러오면 안된다.")
    void find_follow_activities_list() {
        // given
        List<Long> followeIdList = new ArrayList<>();
        UserEntity followerEntity = userEntities.get(0);
        for (int i = 1; i < userEntities.size(); i++) {
            FollowEntity followEntity = FollowEntity.create(followerEntity, userEntities.get(i));
            followRepository.save(followEntity);
            flushAndClearPersistence();
            followeIdList.add(followEntity.getId());
        }
        int initialFollowSize = followeIdList.size();
        int round = 0;
        // when

        // then
        while (round < initialFollowSize) {
            List<FollowActivityDTO> followActivities = followService.getFollowActivities(followeIdList);
            assertEquals(followActivities.size(), initialFollowSize - round);

            Long removeId = followeIdList.get(round++);
            FollowEntity followEntity = followRepository.findById(removeId).orElseThrow();
            followEntity.makeDelete(true);
            flushAndClearPersistence();
        }
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    private UserEntity createUserEntity() {
        return userRepository.save(
                create(createRandomUUID(), createRandomUUID(), createRandomUUID())
        );
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}