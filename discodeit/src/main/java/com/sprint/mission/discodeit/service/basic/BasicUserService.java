package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
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
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponseDto create(UserCreateRequestDto request) {
        if (userRepository.existsByUsername(request.username()))
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        User user = new User(
                request.username(),
                request.email(),
                request.password()
        );
        UserStatus userStatus = UserStatus.builder()
                .userId(user.getId())
                .lastActiveAt(Instant.now())
                .build();

        userRepository.save(user);
        userStatusRepository.save(userStatus);

        boolean online = userStatus.isOnline();

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                online
        );
    }

    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        boolean online = userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                online
        );
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    boolean online = userStatusRepository.findByUserId(user.getId())
                            .map(UserStatus::isOnline)
                            .orElse(false);
                    return new UserResponseDto(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            online
                    );
                })
                .toList();
    }

    @Override
    public UserResponseDto update(UUID userId, UserUpdateRequestDto responseDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        // 엔티티를 가져와 항상 DTO 반환 (결합도 낮춤)
        user.update(responseDto);

        userRepository.save(user);

        boolean online = userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                online
        );
    }


    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }
}
