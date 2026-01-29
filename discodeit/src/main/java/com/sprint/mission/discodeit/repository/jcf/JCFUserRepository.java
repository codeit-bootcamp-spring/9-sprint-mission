package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    private final List<User> data = new ArrayList<>();

    @Override
    public void create(User user) {
        data.add(user);
    }

    @Override
    public User findById(UUID id) {
        for (User u : data) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    @Override
    public User findByLoginId(String loginId) {
        for (User u : data) {
            if (u.getLoginId().equals(loginId)) return u;
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public boolean update(UUID id, String password, String username, String phoneNumber, String nickname) {
        User u = findById(id);
        if (u == null) return false;
        u.update(password, username, phoneNumber, nickname);
        return true;
    }

    @Override
    public boolean delete(UUID id) {
        User u = findById(id);
        if (u == null) return false;
        return data.remove(u);
    }
}


