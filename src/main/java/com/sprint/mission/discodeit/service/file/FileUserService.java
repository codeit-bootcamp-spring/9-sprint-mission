package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileUserService implements UserService {

    private final FileUserRepository fileuserRepository;

    public FileUserService(FileUserRepository fileUserRepository) {
        this.fileuserRepository = fileUserRepository;
    }

    @Override
    public User create(String name, String email, String password) {
        validateDuplicateEmail(email, null);
        User user = new User(name, email, password);
        return fileuserRepository.save(user);
    }

    @Override
    public User findById(UUID userId) {
        User user = fileuserRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return fileuserRepository.findAll();
    }

    @Override
    public User update(UUID userId, String name, String email, String password) {
        User user = findById(userId);

        if (name == null && email == null && password == null) {
            throw new IllegalArgumentException("최소 하나 이상의 수정 값이 필요합니다.");
        }

        validateDuplicateEmail(email, userId);
        user.update(name, email, password);

        return fileuserRepository.update(user);
    }

    @Override
    public void delete(UUID userId) {
        findById(userId);
        fileuserRepository.delete(userId);
    }

    private void validateDuplicateEmail(String email, UUID userId) {
        if (email == null) return;

        Optional<User> found = fileuserRepository.findByEmail(email);
        if (found.isPresent()) {
            if (userId == null || !found.get().getId().equals(userId)) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }
    }
}
