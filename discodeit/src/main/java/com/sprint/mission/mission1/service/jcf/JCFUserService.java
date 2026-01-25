package com.sprint.mission.mission1.service.jcf;

import com.sprint.mission.mission1.entity.User;
import com.sprint.mission.mission1.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public User create(String name, String email, String phoneNumber) {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, name, email, phoneNumber);
        users.put(userId, user);
        return user;
    }

    @Override
    public User read(UUID userId) {
        return users.get(userId);
    }
    @Override
    public List<User> readAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void update(UUID userId, String name, String email, String phoneNumber) {
        User user = users.get(userId);
        if(user != null) {
            user.update(name,email,phoneNumber);
        } else {
            System.out.println("유저가 존재하지 않습니다");
        }
    }

    @Override
    public void delete(UUID userId) {
        User user = users.get(userId);
        if(user != null) {
            System.out.println("유저 " + user + "삭제 완료했습니다");
            users.remove(userId);
        } else {
            System.out.println("유저가 존재하지 않습니다");
        }

    }
}