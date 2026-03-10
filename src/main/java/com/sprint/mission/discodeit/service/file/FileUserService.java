package com.sprint.mission.discodeit.service.file;

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
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Profile("file")
@RequiredArgsConstructor
public class FileUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public UserDto create(UserCreateRequest request, MultipartFile profile) {

        validateDuplicate(request.username(), request.email());

        User user = new User(
            request.username(),
            request.email(),
            request.password()
        );

        handleProfileUpload(user, profile);

        userRepository.save(user);

        UserStatus status = new UserStatus(user);
        userStatusRepository.save(status);

        return userMapper.toDto(user);
    }

    @Override
    public UserDto findById(UUID userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean online = userStatusRepository.findByUser_Id(userId)
            .map(UserStatus::isOnline)
            .orElse(false);

        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAll() {

        return userRepository.findAll().stream()
            .map(user -> {
                boolean online = userStatusRepository.findByUser_Id(user.getId())
                    .map(UserStatus::isOnline)
                    .orElse(false);
                return userMapper.toDto(user);
            })
            .toList();
    }

    @Override
    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest request, MultipartFile profile) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.newEmail() != null && !request.newEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.newEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        if (request.newUsername() != null && !request.newUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(request.newUsername()).isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
        }

        user.update(
            request.newUsername(),
            request.newEmail(),
            request.newPassword()
        );

        handleProfileUpdate(user, profile);

        userRepository.save(user);

        boolean online = userStatusRepository.findByUser_Id(userId)
            .map(UserStatus::isOnline)
            .orElse(false);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getProfile() != null) {
            binaryContentRepository.deleteById(user.getProfile().getId());
        }

        userStatusRepository.findByUser_Id(userId)
            .ifPresent(userStatusRepository::delete);

        userRepository.deleteById(userId);
    }

    private void validateDuplicate(String username, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    private void handleProfileUpload(User user, MultipartFile profile) {
        if (profile == null || profile.isEmpty()) return;

        try {
            BinaryContent binaryContent = new BinaryContent(
                profile.getOriginalFilename(),
                (long) profile.getSize(),
                profile.getContentType() != null ? profile.getContentType() : "application/octet-stream"
            );
            binaryContentRepository.save(binaryContent);

            binaryContentStorage.put(binaryContent.getId(), profile.getBytes());

            user.updateProfile(binaryContent);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleProfileUpdate(User user, MultipartFile profile) {

        if (profile == null || profile.isEmpty()) return;

        if (user.getProfile() != null) {
            binaryContentRepository.deleteById(user.getProfile().getId());
        }

        handleProfileUpload(user, profile);
    }
}