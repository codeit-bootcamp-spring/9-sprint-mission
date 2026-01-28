package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserService implements UserService {

    private final UserRepository userRepository;

    public JCFUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name, String email, String password) {

        validateDuplicateEmail(email, null);

        User user = new User(name, email, password);

        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String name, String email, String password) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (name == null && email == null && password == null) {
            throw new IllegalArgumentException("최소 하나 이상의 수정 값이 필요합니다.");
        }

        validateDuplicateEmail(email, id);

        user.update(name, email, password);

        return userRepository.update(user);
    }

    private void validateDuplicateEmail(String email, UUID id) {
        if (email == null) return;

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (id == null || !id.equals(user.get().getId())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        userRepository.delete(id);
    }
}
