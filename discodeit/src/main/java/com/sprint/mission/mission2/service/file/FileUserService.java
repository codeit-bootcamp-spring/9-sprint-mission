package com.sprint.mission.mission2.service.file;

import com.sprint.mission.mission2.entity.User;
import com.sprint.mission.mission2.repository.UserRepository;
import com.sprint.mission.mission2.repository.file.FileUserRepository;
import com.sprint.mission.mission2.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    UserRepository userRepository;

    public FileUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name, String email, String phoneNumber) {
        UUID id = UUID.randomUUID();
        User user = new User(id, name, email, phoneNumber);
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
        if (user != null) {
            user.update(name, email, phoneNumber);
        }
        else {
            System.out.println("유저가 존재하지 않습니다");
        }
    }

    @Override
    public void delete(UUID userId) {
        userRepository.remove(userId);
    }
}
