package hg.userservice.datasource;

import hg.userservice.core.entity.FollowEntity;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.FollowRepository;
import hg.userservice.core.repository.UserRepository;
import hg.userservice.core.repository.dto.FollowActivityDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static hg.userservice.core.entity.UserEntity.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class FollowRepositoryImplTest {

    @Autowired
    FollowRepositoryImpl followRepository;

    @Autowired
    UserRepository userRepository;

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
    @DisplayName("엔티티 저장시 값이 올바르게 저장되어야 한다.")
    void follow_my_self_will_throws_exception() {
        // given
        UserEntity followerUser = userEntities.get(0);
        UserEntity followeeUser = userEntities.get(1);
        FollowEntity followEntity = FollowEntity.create(followerUser, followeeUser);

        // when
        followRepository.save(followEntity);
        flushAndClearPersistence();

        // then
        FollowEntity follow = followRepository.findByFollowerIdAndFolloweeId(
                followerUser.getId(), followeeUser.getId()
        ).orElseThrow();

        assertEquals(follow.getFollower().getId(), followerUser.getId());
        assertEquals(follow.getFollowee().getId(), followeeUser.getId());
        assertNotNull(follow.getId());
        assertNull(follow.getDeletedAt());
        assertNotNull(follow.getUpdatedAt());
        assertNotNull(follow.getCreatedAt());
    }

    @Test
    @DisplayName("동일한 팔로워, 팔로우를 가진 엔티티는 중복으로 저장할 수 없다.")
    void follow_unique_follower_and_followee() {
        // given
        UserEntity followerUser = userEntities.get(0);
        UserEntity followeeUser = userEntities.get(1);
        FollowEntity followEntity = FollowEntity.create(followerUser, followeeUser);

        // when
        followRepository.save(followEntity);
        flushAndClearPersistence();

        // then
        assertThatThrownBy(() ->
                followRepository.save(FollowEntity.create(followerUser, followeeUser))
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("저장된 팔로우들은 조회가 가능해야 한다.")
    void success_find_exists_follow() {
        // given
        List<long[]> histories = new ArrayList<>();
        for (int i = 0; i < userEntities.size() - 1; i++) {
            UserEntity follower = userEntities.get(i);
            UserEntity followee = userEntities.get(i + 1);
            FollowEntity followEntity = FollowEntity.create(follower, followee);
            followRepository.save(followEntity);
            histories.add(new long[]{follower.getId(), followee.getId()});
        }
        flushAndClearPersistence();
        // when

        // then
        for (long[] history : histories) {
            assertTrue(followRepository.findByFollowerIdAndFolloweeId(history[0], history[1]).isPresent());
        }
    }

    @Test
    @DisplayName("유저의 팔로워 리스트 유저 아이디는 올바르게 조회되어야 한다.")
    void success_find_follower_list() {
        // given
        UserEntity targetUser = userEntities.get(0);
        List<Long> followerListId = new ArrayList<>();
        for (int i = 1; i < userEntities.size(); i++) {
            FollowEntity followEntity = FollowEntity.create(targetUser, userEntities.get(i));
            followerListId.add(userEntities.get(i).getId());
            followRepository.save(followEntity);
        }
        flushAndClearPersistence();

        // when

        // then
        while (!followerListId.isEmpty()) { // 하나씩 팔로우를 취소하면서, 조회가 정상적으로 되는지 확인.
            List<Long> followeeIdList = followRepository.findFolloweeIdList(targetUser.getId());
            assertEquals(followerListId.size(), followeeIdList.size());
            Long followeeId = followeeIdList.remove(0);
            FollowEntity followEntity = followRepository.findByFollowerIdAndFolloweeId(targetUser.getId(), followeeId)
                    .orElseThrow();

            followEntity.makeDelete(true);
            followerListId.remove((Object) followeeId);
            flushAndClearPersistence();
        }
    }

    @Test
    @DisplayName("팔로우 내역은 올바르게 조회되어야 한다.")
    void success_find_follow_activities() {
        // given
        UserEntity targetUser = userEntities.get(0);
        List<Long> followerIdList = new ArrayList<>();
        List<Long> removedFollowIdList = new ArrayList<>();
        for (int i = 1; i < userEntities.size(); i++) {
            FollowEntity followEntity = FollowEntity.create(targetUser, userEntities.get(i));
            followRepository.save(followEntity);
            flushAndClearPersistence();
            followerIdList.add(followEntity.getId());
        }
        flushAndClearPersistence();

        // when

        // then
        while (!followerIdList.isEmpty()) { // 하나씩 팔로우를 취소하면서, 내역이 정상적으로 조회 되는지 확인.
            List<FollowActivityDTO> followActivities = followRepository.findFollowActivity(followerIdList);
            assertEquals(followActivities.size(), followerIdList.size());
            Long remove = followerIdList.remove(0);
            removedFollowIdList.add(remove);
            FollowEntity followEntity = followRepository.findById(remove).orElseThrow();
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