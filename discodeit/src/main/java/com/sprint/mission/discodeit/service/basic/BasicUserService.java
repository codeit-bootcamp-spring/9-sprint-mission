package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentService binaryContentService;

    @Override
    public User create(UserCreateRequest request, Optional<BinaryContentCreateRequest> profileRequest) {
        UUID profileId = profileRequest.map(req -> {
            BinaryContent savedContent = binaryContentService.create(req);
            return savedContent.getId();
        }).orElse(null);
        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profileId
        );

        User savedUser = userRepository.save(user);
        userStatusRepository.save(new UserStatus(savedUser.getId()));

        return savedUser;
    }

    @Override
    public UserStatusResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseGet(() -> {
                    return new UserStatus(userId);
                });

        return new UserStatusResponse(
                user.getId().toString(),
                user.getUsername(),
                status.isOnline(),
                status.getUpdatedAt() != null ? status.getUpdatedAt().toString() : "N/A"
        );
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userRepository.findAll().stream()
                //.limit(2)
                .map(user -> find(user.getId()))
                .toList();
    }
/*  유저에 있는 모든 데이터를 가져와서 스트림 형식으로 바꾸고 .map부분에서 user객체를 하나씩 꺼내고
 find 메소드를 이용해서 id로 추출한 유저정보를 UserStatusResponse변환하고 리스트 형식으로 만든다
 */
    @Override
    public User update(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        user.update(
                request.username(),
                request.email(),
                request.password()
        );
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found");
        }
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

}
