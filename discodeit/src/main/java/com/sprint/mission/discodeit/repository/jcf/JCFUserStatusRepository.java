package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.status.UserStatusInterface;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public class JCFUserStatusRepository implements UserStatusInterface {
    private final Map<UUID, UserStatus> store = new HashMap<>();

    @Override
    public void save(UserStatus userStatus) {
        store.put(userStatus.getUserId(), userStatus);
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return store.values().stream().filter(userStatus -> userStatus.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<UserStatus> findByUser(UUID userId) {
        return store.values().stream().filter(userStatus -> userStatus.getUserId().equals(userId)).findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(UUID id) {
        store.values().removeIf(userStatus -> userStatus.getId().equals(id));
    }
}
