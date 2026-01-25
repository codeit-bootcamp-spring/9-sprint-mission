package com.sprint.mission.mission1.service;

import com.sprint.mission.mission1.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    public User create(String name, String email, String phoneNumber);
    public User read(UUID userId);
    public List<User> readAll();
    public void update(UUID userId, String name, String email, String phoneNumber);
    public void delete(UUID userId);
}