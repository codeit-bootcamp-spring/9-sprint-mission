package com.sprint.mission.mission2.service.jcf;

import com.sprint.mission.mission2.entity.User;
import com.sprint.mission.mission2.repository.UserRepository;
import com.sprint.mission.mission2.repository.jcf.JCFUserRepository;
import com.sprint.mission.mission2.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    UserRepository userRepository = new JCFUserRepository();

    @Override
    public User create(String name, String email, String phoneNumber) {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, name, email, phoneNumber);
        userRepository.save(user);
        return user;
    }

    @Override
    public User read(UUID userId) {
        return userRepository.read(userId);
    }
    @Override
    public List<User> readAll() {
        return userRepository.readAll();
    }

    @Override
    public void update(UUID userId, String name, String email, String phoneNumber) {
        User user = userRepository.read(userId);
        if(user != null) {
            user.update(name,email,phoneNumber);
        } else {
            System.out.println("유저가 존재하지 않습니다");
        }
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.read(userId);
        if(user != null) {
            System.out.println("유저 " + user + "삭제 완료했습니다");
            userRepository.remove(userId);
        } else {
            System.out.println("유저가 존재하지 않습니다");
        }

    }
}