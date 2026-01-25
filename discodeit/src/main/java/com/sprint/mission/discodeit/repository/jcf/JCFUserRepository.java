package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> userMap = new ConcurrentHashMap<>();
    private final Map<String, User> nameMap = new ConcurrentHashMap<>();

    private JCFUserRepository() {}

    private static class InstanceHolder {
        private static final JCFUserRepository INSTANCE = new JCFUserRepository();
    }

    public static JCFUserRepository getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void save(User user) {
        userMap.put(user.getId(), user);
        nameMap.put(user.getDisplayName(), user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public Optional<User> findByDisplayName(String displayName) {
        return Optional.ofNullable(nameMap.get(displayName));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }


    @Override
    public void delete(UUID id) {
        User removed = userMap.remove(id);
        if (removed != null) {
            nameMap.remove(removed.getDisplayName());
        }
    }
}