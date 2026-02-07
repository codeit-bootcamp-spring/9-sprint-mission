package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserStatusRepository implements UserStatusRepository {

    private final List<UserStatus> userStatuses = new ArrayList<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatuses.add(userStatus);
        return userStatus;
    }

    @Override
    public UserStatus find(UUID id) {
        for (UserStatus userStatus : userStatuses) {
            if (userStatus.getId().equals(id)) {
                return userStatus;
            }
        }
        return null;
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        for (UserStatus userStatus : userStatuses) {
            if (userStatus.getUserId().equals(userId)) {
                return userStatus;
            }
        }
        return null;
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(userStatuses);
    }

    @Override
    public void delete(UUID id) {
        userStatuses.removeIf(userStatus -> userStatus.getId().equals(id));
    }
}
