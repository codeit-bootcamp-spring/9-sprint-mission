package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserStatusDto.Response create(UUID userId) {
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("해당 유저의 상태가 이미 존재합니다.");
        }

        UserStatus status = new UserStatus(userId);
        userStatusRepository.save(status);
        return convertToResponse(status);
    }

    @Override
    public void updateByUserId(UUID userId) {
        userStatusRepository.findByUserId(userId).ifPresent(status -> {
            status.updateLastAccessedAt(); // 접속 시간 현재로 갱신
            userStatusRepository.save(status);
        });
    }

    @Override
    public boolean isUserOnline(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .map(status -> {
                    Instant lastAccessed = status.getLastAccessedAt();
                    return lastAccessed != null &&
                            lastAccessed.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
                }).orElse(false);
    }

    @Override
    public Optional<UserStatusDto.Response> findByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId).map(this::convertToResponse);
    }

    @Override
    public List<UserStatusDto.Response> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        userStatusRepository.findByUserId(userId)
                .ifPresent(s -> userStatusRepository.delete(s.getId()));
    }

    private UserStatusDto.Response convertToResponse(UserStatus status) {
        return new UserStatusDto.Response(
                status.getId(),
                status.getUserId(),
                status.getLastAccessedAt()
        );
    }
}