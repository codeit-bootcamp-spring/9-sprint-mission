package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@Profile("!jcf")
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserCreateRequest request, MultipartFile profile) {

        validateDuplicate(request.username(), request.email());

        User user = new User(
            request.username(),
            request.email(),
            request.password()
        );

        if (profile != null && !profile.isEmpty()) {
            try {
                BinaryContent binaryContent = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getBytes(),
                    profile.getContentType() != null ? profile.getContentType() : "application/octet-stream"
                );
                binaryContentRepository.save(binaryContent);
                user.updateProfile(binaryContent.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        userRepository.save(user);

        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

        return userMapper.toDto(user, true);
    }

    @Override
    public UserDto findById(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        boolean online = userStatusRepository.findByUserId(userId)
            .map(UserStatus::isOnline)
            .orElse(false);

        return userMapper.toDto(user, online);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
            .map(user -> {
                boolean online = userStatusRepository.findByUserId(user.getId())
                    .map(UserStatus::isOnline)
                    .orElse(false);
                return userMapper.toDto(user, online);
            })
            .toList();
    }

    @Override
    public UserDto update(UUID userId, UserUpdateRequest request, MultipartFile profile) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (request.newEmail() != null && !request.newEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.newEmail()).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 email입니다.");
            }
        }

        if (request.newUsername() != null && !request.newUsername().equals(user.getName())) {
            if (userRepository.findByUsername(request.newUsername()).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 username입니다.");
            }
        }

        user.update(request.newUsername(), request.newEmail(), request.newPassword());

        if (profile != null && !profile.isEmpty()) {
            if (user.getProfileId() != null) {
                binaryContentRepository.delete(user.getProfileId());
            }
            try {
                BinaryContent binaryContent = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getBytes(),
                    profile.getContentType() != null ? profile.getContentType() : "application/octet-stream"
                );
                binaryContentRepository.save(binaryContent);
                user.updateProfile(binaryContent.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        userRepository.update(user);

        boolean online = userStatusRepository.findByUserId(userId)
            .map(UserStatus::isOnline)
            .orElse(false);

        return userMapper.toDto(user, online);
    }

    @Override
    public void delete(UUID userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }

        userStatusRepository.findByUserId(userId)
            .ifPresent(status -> userStatusRepository.delete(status.getId()));

        userRepository.delete(userId);
    }

    private void validateDuplicate(String username, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 username입니다.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 email입니다.");
        }
    }
}