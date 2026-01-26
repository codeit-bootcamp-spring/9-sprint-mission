package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {

    private final UserRepository userRepository;

    public FileUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String username, String mail, String phone) {
        User user = new User(username, mail, phone);
        return userRepository.save(user);
    }

    @Override
    public User find(UUID id) {
        return userRepository.find(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User updated(UUID id, String username, String email, String phone) {
        User user = userRepository.find(id);
        if (user != null) {
            user.update(username, email, phone);
        }
        return user;
    }

    @Override
    public void delete(UUID id) {
        userRepository.delete(id);
    }
}
