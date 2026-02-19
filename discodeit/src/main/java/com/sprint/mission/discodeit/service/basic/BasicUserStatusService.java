package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusView;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessException;
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
    public UserStatusView create(UserStatusCreateRequest request) {
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
            throw new BusinessException("UserStatus already exists. userId=" + userId);
        }

        UserStatus userStatus = new UserStatus(UUID.randomUUID(), userId, Instant.now());
        UserStatus saved = userStatusRepository.save(userStatus);
        return toView(saved);
    }

    @Override
    public Optional<UserStatusView> findById(UUID userStatusId) {
        if (userStatusId == null) {
            throw new IllegalArgumentException("userStatusId must not be null");
        }
        return userStatusRepository.findById(userStatusId).map(this::toView);
    }

    @Override
    public List<UserStatusView> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public UserStatusView update(UserStatusUpdateRequest request) {
        if (request == null || request.userStatusId() == null || request.params() == null) {
            throw new IllegalArgumentException("request.userStatusId and request.params are required");
        }
        if (request.params().lastActiveAt() == null) {
            throw new IllegalArgumentException("lastActiveAt must not be null");
        }

        UserStatus existing = userStatusRepository.findById(request.userStatusId())
                .orElseThrow(() -> new NotFoundException("UserStatus not found. id=" + request.userStatusId()));

        existing.touch(request.params().lastActiveAt());
        UserStatus saved = userStatusRepository.save(existing);
        return toView(saved);
    }

    @Override
    public UserStatusView updateByUserId(UUID userId, UserStatusUpdateRequest.Params params) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        Instant lastActiveAt = Instant.now();
        if (params != null && params.lastActiveAt() != null) {
            lastActiveAt = params.lastActiveAt();
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found. id=" + userId);
        }

        UserStatus existing = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("UserStatus not found. userId=" + userId));

        existing.touch(lastActiveAt);
        UserStatus saved = userStatusRepository.save(existing);
        return toView(saved);
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

    private UserStatusView toView(UserStatus status) {
        return new UserStatusView(
                status.getId(),
                status.getCreatedAt(),
                status.getUpdatedAt(),
                status.getUserId(),
                status.getLastSeenAt(),
                status.isOnlineNow()
        );
    }
}