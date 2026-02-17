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
    public UserResponse create(CreateUserRequest request) {
        validateDuplicate(request.username(), request.email());

        User user = new User(
                request.username(),
                request.email(),
                request.password()
        );

        if (request.profileImageId() != null) {
            binaryContentRepository.findById(request.profileImageId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파일입니다."));
            user.updateProfile(request.profileImageId());
        }

        userRepository.save(user);
        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

        return UserResponse.from(user, status);
    }

    @Override
    public UserResponse findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return UserResponse.from(
                user,
                userStatusRepository.findByUserId(userId).orElse(null)
        );
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status =
                            userStatusRepository.findByUserId(user.getId())
                                    .orElse(null);

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
    public UserResponse update(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.email()).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 email입니다.");
            }
        }

        if (request.username() != null && !request.username().equals(user.getName())) {
            if (userRepository.findByUsername(request.username()).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 username입니다.");
            }
        }

        user.update(
                request.username(),
                request.email(),
                request.password()
        );

        if (request.profileImageId() != null) {
            binaryContentRepository.findById(request.profileImageId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파일입니다."));
            user.updateProfile(request.profileImageId());
        }

        userRepository.update(user);

        return UserResponse.from(
                user,
                userStatusRepository.findByUserId(user.getId()).orElse(null)
        );
    }

    @Override
    public void delete(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

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
