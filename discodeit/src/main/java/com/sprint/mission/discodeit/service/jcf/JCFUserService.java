package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userMap = new ConcurrentHashMap<>();
    private final Map<String, User> nameMap = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        nameMap.put(user.getDisplayName(), user);
        return user;
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

        // 기존 객체의 name 필드가 이미 바뀌었을 수 있으므로, nameMap에서 해당 Value를 가진 Entry를 찾아 삭제합니다.
        nameMap.entrySet().removeIf(entry -> entry.getValue().getId().equals(newUser.getId()));

        // 새로운 정보로 인덱스 재등록
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