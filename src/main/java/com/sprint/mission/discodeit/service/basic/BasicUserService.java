package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ProfileImageRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        validateDuplicate(request.username(), request.email());

        User user = new User(
                request.username(),
                request.email(),
                request.password()
        );

        if (request.profileImage() != null) {
            BinaryContent profile = binaryContentRepository.save(
                    new BinaryContent(
                            request.profileImage().data(),
                            request.profileImage().contentType()
                    )
            );
            user.updateProfile(profile.getId());
        }

        userRepository.save(user);

        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

        return UserResponse.from(user, status);
    }

    @Override
    public UserResponse findById(UUID userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        Optional<UserStatus> statusOpt = userStatusRepository.findByUserId(userId);
        return UserResponse.from(user, statusOpt.orElse(null));
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(u -> {
                    Optional<UserStatus> s = userStatusRepository.findByUserId(u.getId());
                    return UserResponse.from(u, s.orElse(null));
                })
                .toList();
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = userRepository.findById(request.userId());
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
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
        user.update(request.username(), request.email(), request.password());

        if (request.profileImage() != null) {
            UUID oldProfileId = user.getProfileId();
            BinaryContent newProfile = binaryContentRepository.save(
                    new BinaryContent(request.profileImage().data(), request.profileImage().contentType()));
            user.updateProfile(newProfile.getId());
            if (oldProfileId != null) {
                binaryContentRepository.delete(oldProfileId);
            }
        }

        userRepository.update(user);
        Optional<UserStatus> statusOpt = userStatusRepository.findByUserId(request.userId());
        return UserResponse.from(user, statusOpt.orElse(null));
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        userStatusRepository.findByUserId(userId).ifPresent(s -> userStatusRepository.delete(s.getId()));
        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }
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
