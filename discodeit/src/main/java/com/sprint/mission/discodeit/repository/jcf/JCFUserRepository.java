package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class JCFUserRepository implements UserRepository {

    private final List<User> data;

    public JCFUserRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public void create(User user) {
        data.add(user);
    }

    @Override
    public User findById(UUID id) {
        for (User user : data) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }


    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public boolean update(UUID id, String nickname, String phoneNumber, String password) {
        User found = findById(id);
        if (found == null) {
            return false;
        }
        found.update(nickname, phoneNumber, password);
        return true;
    }

    @Override
    public boolean delete(UUID id) {
        User found = findById(id);
        if (found == null) {
            return false;
        }
        return data.remove(found);
    }
}


