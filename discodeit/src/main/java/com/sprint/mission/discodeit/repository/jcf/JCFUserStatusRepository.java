package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Primary
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> store = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        store.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return store.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return store.values().stream()
                .anyMatch(status -> status.getUserId().equals(userId));
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }
}
