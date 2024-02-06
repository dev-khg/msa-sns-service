package hg.activityservice.datasource;

import com.example.commonproject.activity.ActivityType;
import hg.activityservice.core.entity.ActivityEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static hg.activityservice.core.entity.ActivityEntity.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ActivityRepositoryImplTest {
    @Autowired
    ActivityRepositoryImpl activityRepository;
    @PersistenceContext
    EntityManager em;

    ActivityType[] values = ActivityType.values();
    Random random = new Random();

    @Test
    @DisplayName("저장된 활동 엔티티는 유저 아이디와 타겟아이디로 조회가 가능해야한다.")
    void saved_data_can_find_by_user_id_and_target_id() {
        // given
        long userId = getRandomLong();
        long targetId = getRandomLong();
        ActivityType type = getRandomActivityType();
        ActivityEntity activityEntity = createActivityEntity(userId, targetId, type);

        // when
        activityRepository.save(activityEntity);
        flushAndClearPersistence();

        // then
        ActivityEntity savedEntity =
                activityRepository.findByUserIdAndTargetIdAndType(userId, targetId, type)
                        .orElseThrow();

        assertEquals(savedEntity.getUserId(), userId);
        assertEquals(savedEntity.getTargetId(), targetId);
        assertEquals(savedEntity.getType(), type);
        assertNotNull(savedEntity.getId());
        assertNotNull(savedEntity.getCreatedAt());
        assertNull(savedEntity.getDeletedAt());
    }

    @Test
    @DisplayName("저장된 활동을 유저아이디로 조회시 정상적으로 조회되어야 한다.")
    void find_by_user_id_list_then_return_properly() {
        // given
        int savedSize = 10;
        long userId = getRandomLong();
        List<ActivityEntity> savedEntity = new ArrayList<>(savedSize);
        for (int i = 0; i < savedSize; i++) {
            ActivityEntity activityEntity =
                    create(userId, getRandomLong(), getRandomActivityType());

            activityRepository.save(activityEntity);
            savedEntity.add(activityEntity);
        }
        flushAndClearPersistence();
        savedEntity.sort(Comparator.comparing(ActivityEntity::getCreatedAt));

        // when
        PageRequest pageable = PageRequest.of(0, savedSize + 1);
        List<ActivityEntity> activityEntities
                = activityRepository.findActivitiesByUserIdIn(List.of(userId), pageable);

        // then
        assertEquals(activityEntities.size(), savedEntity.size());

        for (int i = 0; i < savedSize; i++) {
            ActivityEntity expected = activityEntities.get(i);
            ActivityEntity actual = savedEntity.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getTargetId(), actual.getTargetId());
        }
    }

    private ActivityEntity createActivityEntity(long userId, long targetId, ActivityType type) {
        return create(userId, targetId, type);
    }

    private long getRandomLong() {
        return random.nextLong(0, 10000);
    }

    private ActivityType getRandomActivityType() {
        return values[random.nextInt(values.length)];
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }
}