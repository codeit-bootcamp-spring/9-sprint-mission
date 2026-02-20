package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> data = new HashMap<>();
    // userId -> statusId (도메인 규칙: 유저별 상태는 1개)
    private final Map<UUID, UUID> userIdIndex = new HashMap<>();

    @Override
    public UserStatus save(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status is null");
        }

        UUID userId = status.getUserId();
        UUID statusId = status.getId();

        UUID existingStatusId = userIdIndex.get(userId);
        if (existingStatusId != null && !existingStatusId.equals(statusId)) {
            data.remove(existingStatusId);
        }

        data.put(statusId, status);
        userIdIndex.put(userId, statusId);
        return status;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID id) {
        if (id == null) return;

        UserStatus removed = data.remove(id);
        if (removed == null) return;

        UUID userId = removed.getUserId();
        UUID indexedStatusId = userIdIndex.get(userId);
        if (indexedStatusId != null && indexedStatusId.equals(id)) {
            userIdIndex.remove(userId);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) return false;
        return data.containsKey(id);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        if (userId == null) return Optional.empty();

        UUID statusId = userIdIndex.get(userId);
        if (statusId == null) return Optional.empty();

        return Optional.ofNullable(data.get(statusId));
    }
}
