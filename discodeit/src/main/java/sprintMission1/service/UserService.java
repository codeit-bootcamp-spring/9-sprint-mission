package sprintMission1.service;

import sprintMission1.entity.User;

import java.util.UUID;

public interface UserService {
    User create(String userName);
    void read(UUID userId);
    void readAll();
    void update(UUID userId, String userName);
    void delete(UUID id);
}
