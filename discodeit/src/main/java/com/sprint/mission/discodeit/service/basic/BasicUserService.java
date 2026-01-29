package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.DTO.UserCreatRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.status.BinaryContentInterface;
import com.sprint.mission.discodeit.status.UserStatusInterface;
import com.sprint.mission.discodeit.status.add.BinaryContent;
import com.sprint.mission.discodeit.status.add.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
//@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusInterface userStatusInterface;
    private final BinaryContentInterface binaryContentInterface;

    public BasicUserService(
            UserRepository userRepository,
            UserStatusInterface userStatusInterface,
            BinaryContentInterface binaryContentInterface
    ){
        this.userRepository = userRepository;
        this.userStatusInterface = userStatusInterface;
        this.binaryContentInterface = binaryContentInterface;
    }

    public User creat(UserCreatRequest request) {

        if (userRepository.existsByUserName(request.username())) {
            throw new IllegalArgumentException("중복된 유저입니다.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("중복된 이메일립니다.");
        }

        User user = new User(
                request.username(),
                request.email(),
                request.password()
        );

        userRepository.save(user);

        UserStatus status = new UserStatus(
                UUID.randomUUID(),
                user.getId(),
                Instant.now(),
                Instant.now(),
                Instant.now()
        );
        userStatusInterface.save(status);

        if (request.profileImage() != null) {
            BinaryContent profile = new BinaryContent(
                    UUID.randomUUID(),
                    user.getId(),
                    null,
                    Instant.now()
            );
            binaryContentInterface.save(profile);
        }
        return user;
    }
//    @Override
//    public User create(String username, String email, String password) {
//        User user = new User(username, email, password);
//        return userRepository.save(user);
//    }

    @Override
    public User find(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        user.update(newUsername, newEmail, newPassword);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }
}
