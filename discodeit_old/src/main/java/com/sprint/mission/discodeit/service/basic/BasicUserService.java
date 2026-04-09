package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {


    private final UserRepository userRepository;

    @Override
    public void join(User user) {
        userRepository.create(user);
    }
    @Override
    public User findById(UUID id) {
        return userRepository.findById(id);
    }
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    @Override
    public boolean update(UUID id, String nickname, String phoneNumber, String password) {
        return userRepository.update(id, nickname, phoneNumber, password);
    }

    @Override
    public boolean delete(UUID id) {
        return userRepository.delete(id);
    }
}
