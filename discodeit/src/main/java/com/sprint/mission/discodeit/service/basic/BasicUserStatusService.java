package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        if (request == null || request.target() == null) {
            throw new IllegalArgumentException("request.target must not be null");
        }

        UUID userId = request.target().userId();
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found. id=" + userId);
        }

        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("UserStatus already exists. userId=" + userId);
        }

        UserStatus userStatus = new UserStatus(UUID.randomUUID(), userId, Instant.now());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        if (userStatusId == null) {
            throw new IllegalArgumentException("userStatusId must not be null");
        }
        return userStatusRepository.findById(userStatusId);
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UserStatusUpdateRequest request) {
        if (request == null || request.userStatusId() == null || request.params() == null) {
            throw new IllegalArgumentException("request.userStatusId and request.params are required");
        }
        if (request.params().lastActiveAt() == null) {
            throw new IllegalArgumentException("lastActiveAt must not be null");
        }

        UserStatus existing = userStatusRepository.findById(request.userStatusId())
                .orElseThrow(() -> new NotFoundException("UserStatus not found. id=" + request.userStatusId()));

        existing.touch(request.params().lastActiveAt());
        return userStatusRepository.save(existing);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest.Params params) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("params must not be null");
        }
        if (params.lastActiveAt() == null) {
            throw new IllegalArgumentException("lastActiveAt must not be null");
        }

        // user 검증
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found. id=" + userId);
        }

        UserStatus existing = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("UserStatus not found. userId=" + userId));

        existing.touch(params.lastActiveAt());
        return userStatusRepository.save(existing);
    }

    @Override
    public void delete(UUID userStatusId) {
        if (userStatusId == null) {
            throw new IllegalArgumentException("userStatusId must not be null");
        }

        if (!userStatusRepository.existsById(userStatusId)) {
            throw new NotFoundException("UserStatus not found. id=" + userStatusId);
        }

        userStatusRepository.delete(userStatusId);
    }

    @Override
    public boolean existsById(UUID userStatusId) {
        if (userStatusId == null) return false;
        return userStatusRepository.existsById(userStatusId);
    }
}