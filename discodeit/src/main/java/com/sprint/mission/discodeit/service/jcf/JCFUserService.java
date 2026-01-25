package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userMap = new ConcurrentHashMap<>();
    private final Map<String, User> nameMap = new ConcurrentHashMap<>();

    private JCFUserService() {}

    private static class InstanceHolder {
        private static final JCFUserService INSTANCE = new JCFUserService();
    }

    public static JCFUserService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        nameMap.put(user.getDisplayName(), user);
        return user;
    }

    @Override
    public List<User> findAllByDisplayNameKeyword(String keyword) {
        return userMap.values().stream()
                .filter(user -> user.getDisplayName().contains(keyword))
                .toList();
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
    public synchronized void update(User newUser) {
        if (!userMap.containsKey(newUser.getId())) return;
        nameMap.entrySet().removeIf(entry -> entry.getValue().getId().equals(newUser.getId()));
        userMap.put(newUser.getId(), newUser);
        nameMap.put(newUser.getDisplayName(), newUser);
    }

    @Override
    public boolean delete(UUID id) {
        User user = userMap.remove(id);
        if (user != null) {
            nameMap.remove(user.getDisplayName());
            return true;
        }
        return false;
    }
}