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
    public UserResponse create(UserCreateRequest request) {
        validateDuplicate(request.username(), request.email());

        BinaryContent profile = null;
        if (request.profileImage() != null) {
            profile = binaryContentRepository.save(
                    new BinaryContent(
                            request.profileImage().data(),
                            request.profileImage().contentType()
                    )
            );
        }

        User user = new User(
                request.username(),
                request.email(),
                request.password()
        );

        if (profile != null) {
            user.updateProfile(profile.getId());
        }

        userRepository.save(user);
        userStatusRepository.save(new UserStatus(user.getId()));

        return UserResponse.from(user, null);
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

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user ->
                        UserResponse.from(
                                user,
                                userStatusRepository.findByUserId(user.getId()).orElse(null)
                        )
                )
                .toList();
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        user.update(
                request.username(),
                request.email(),
                request.password()
        );

        if (request.profileImage() != null) {
            UUID oldProfileId = user.getProfileId();

            BinaryContent newProfile = binaryContentRepository.save(
                    new BinaryContent(
                            request.profileImage().data(),
                            request.profileImage().contentType()
                    )
            );

            user.updateProfile(newProfile.getId());

            if (oldProfileId != null) {
                binaryContentRepository.delete(oldProfileId);
            }
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
