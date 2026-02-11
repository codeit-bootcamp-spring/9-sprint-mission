package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.UserStatus;
import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        // 1. 유저 존재 확인
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("유저를 찾을 수 없습니다: " + request.userId());
        }

        // 2. 중복 확인 (유저 아이디로 찾아야 함!)
        // [수정됨] findById -> findByUserId
        UserStatus existingStatus = userStatusRepository.findByUserId(request.userId());
        if (existingStatus != null) {
            throw new IllegalArgumentException("이미 해당 유저의 상태 정보가 존재합니다.");
        }

        // 3. 생성 및 저장
        UserStatus userStatus = new UserStatus(
                UUID.randomUUID(),
                request.userId(),
                Instant.now(), // 생성 시점 = 마지막 접속
                Instant.now(),
                Instant.now()
        );
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public UserStatusResponse find(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId);
        if (userStatus == null) {
            throw new NoSuchElementException("상태 정보를 찾을 수 없습니다: " + userStatusId);
        }
        return toResponse(userStatus);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserStatusResponse update(UserStatusUpdateRequest request) {
        // [수정됨] UserStatus의 ID로 찾아야 함 -> findById
        UserStatus userStatus = userStatusRepository.findById(request.id());
        if (userStatus == null) {
            throw new NoSuchElementException("상태 정보를 찾을 수 없습니다.");
        }

        userStatus.update(request.lastSeenAt());
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId) {
        // [수정됨] 유저 ID로 찾아야 함 -> findByUserId
        UserStatus userStatus = userStatusRepository.findByUserId(userId);
        if (userStatus == null) {
            throw new NoSuchElementException("해당 유저의 상태 정보를 찾을 수 없습니다: " + userId);
        }

        // 마지막 접속 시간을 '현재'로 갱신 (Heartbeat 기능)
        userStatus.update(Instant.now());
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if (userStatusRepository.findById(userStatusId) == null) {
            throw new NoSuchElementException("상태 정보를 찾을 수 없습니다.");
        }
        userStatusRepository.deleteById(userStatusId);
    }

    // 변환기
    private UserStatusResponse toResponse(UserStatus userStatus) {
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastSeenAt(),
                userStatus.isOnline()
        );
    }
}