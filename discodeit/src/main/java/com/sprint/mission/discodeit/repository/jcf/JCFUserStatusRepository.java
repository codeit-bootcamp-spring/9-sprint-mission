package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> userStatusMap;

    public JCFUserStatusRepository(){
        userStatusMap = new HashMap<>();
    }

    @Override
    public void save(UserStatus userStatus) {
        UUID id = userStatus.getId();
        userStatusMap.put(id, userStatus);
    }

    @Override
    public boolean remove(UUID id) {
        System.out.println("UserStatus 삭제 - ID: " + id);
        return (userStatusMap.remove(id) != null);
    }

    @Override
    public Optional<UserStatus> findByID(UUID id) {
        return Optional.ofNullable(userStatusMap.get(id));
    }


    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(userStatusMap.values());
    }
}
