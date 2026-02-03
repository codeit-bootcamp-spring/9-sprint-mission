package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.DTO.User.UserCreatRequest;
import com.sprint.mission.discodeit.service.DTO.User.UserResponse;
import com.sprint.mission.discodeit.service.DTO.User.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.status.BinaryContentInterface;
import com.sprint.mission.discodeit.status.UserStatusInterface;
import com.sprint.mission.discodeit.status.adds.BinaryContent;
import com.sprint.mission.discodeit.status.adds.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusInterface userStatusInterface;
    private final BinaryContentInterface binaryContentInterface;


    public User create(UserCreatRequest request) {

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


    private UserResponse userResponse(User user, UserStatus status) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                status.online()
        );
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException());

        UserStatus status = userStatusInterface.findByUser(userId)
                .orElseThrow();

        return userResponse(user, status);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user-> {
                    UserStatus status = userStatusInterface
                            .findByUser(user.getId())
                            .orElseThrow(() -> new NoSuchElementException(
                                    "없는 유저입니다" + user.getId()
                            ));
                    return userResponse(user, status);
                })
                .toList();
    }


    public UserResponse update(UserUpdateRequest request){
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("없는 유저"));

        user.update(
                request.username(),
                request.email(),
                request.password()
        );
        userRepository.save(user);

        if (request.profileImage() != null) {
            BinaryContent profile = new BinaryContent(
                    UUID.randomUUID(),
                    user.getId(),
                    null,
                    Instant.now()
            );
            binaryContentInterface.save(profile);
        }

        UserStatus status = userStatusInterface.findByUser(user.getId())
                .orElseThrow();

        return userResponse(user, status);
    }

    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("없는 유저");
        }
        binaryContentInterface.deleteById(userId);

        userStatusInterface.deleteById(userId);

        userRepository.deleteById(userId);
    }
}
