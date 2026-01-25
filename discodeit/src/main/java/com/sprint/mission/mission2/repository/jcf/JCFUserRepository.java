package com.sprint.mission.mission2.repository.jcf;

import com.sprint.mission.mission2.entity.User;
import com.sprint.mission.mission2.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User read(UUID id) {
        return users.get(id);
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void remove(UUID id) {
        users.remove(id);
    }
}
