package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Profile("jcf")
@RequiredArgsConstructor
public class JCFUserService implements UserService {

    private final JCFUserRepository userRepository;
    private final JCFUserStatusRepository userStatusRepository;
    private final JCFBinaryContentRepository binaryContentRepository;

    @Override
    public User create(UserCreateRequest request, MultipartFile profile) {
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

        // UserStatus 생성
        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

        return user; // UserResponse 대신 User 반환
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
            .map(user -> {
                UserStatus status =
                    userStatusRepository.findByUserId(user.getId()).orElse(null);

                return new UserDto(
                    user.getId(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getName(),
                    user.getEmail(),
                    user.getProfileId(),
                    status != null && status.isOnline()
                );
            })
            .toList();
    }

    @Override
    public User update(UUID userId, UserUpdateRequest request, MultipartFile profile) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.newEmail() != null && !request.newEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.newEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        if (request.newUsername() != null && !request.newUsername().equals(user.getName())) {
            if (userRepository.findByUsername(request.newUsername()).isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
        }

        user.update(
            request.newUsername(),
            request.newEmail(),
            request.newPassword()
        );

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

        return user;
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }

        userStatusRepository.findByUserId(userId)
            .ifPresent(status -> userStatusRepository.delete(status.getId()));

        userRepository.delete(userId);
    }

    private void validateDuplicate(String username, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}