package main.java.com.sprint.mission.discodeit.repository.jcf;

import main.java.com.sprint.mission.discodeit.entity.User;
import main.java.com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> userMap = new HashMap<>();

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findId(UUID userId) {
        User user = userMap.get(userId);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void deleteById(UUID id) {
        userMap.remove(id);
    }
}