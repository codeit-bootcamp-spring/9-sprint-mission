package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.*;

public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> userStatusMap = new HashMap<>();

    @Override
    public void save(UserStatus userStatus) {
        userStatusMap.put(userStatus.getId(), userStatus);
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(userStatusMap.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        for (UserStatus status : userStatusMap.values()) {
            if (status.getUserId().equals(userId)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(userStatusMap.values());
    }

    @Override
    public void delete(UUID id) {
        userStatusMap.remove(id);
    }
}