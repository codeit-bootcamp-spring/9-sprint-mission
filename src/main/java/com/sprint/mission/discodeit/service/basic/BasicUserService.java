package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Primary
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password);
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = findById(userId);
        user.update(newUsername, newEmail, newPassword);
        return userRepository.update(user);
    }

    @Override
    public void delete(UUID userId) {
        findById(userId);
        userRepository.delete(userId);
    }
}
