package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * UserStatusService 인터페이스의 기본 구현체.
 * 사용자 상태(온라인/오프라인) 관리 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    /**
     * 사용자 상태 정보를 저장하고 조회하는 리포지토리
     */
    private final UserStatusRepository userStatusRepository;

    /**
     * 사용자 존재 여부 확인을 위한 리포지토리
     */
    private final UserRepository userRepository;

    /**
     * 사용자 상태를 생성합니다.
     * 사용자의 존재 여부를 확인하고, 중복 생성을 방지합니다.
     *
     * @param request 사용자 상태 생성 요청 정보
     * @return 생성된 사용자 상태
     * @throws NoSuchElementException 사용자를 찾을 수 없을 경우
     * @throws IllegalArgumentException 해당 사용자의 상태가 이미 존재할 경우
     */
    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        // User 존재 여부 확인
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User not found: " + request.userId());
        }
        // 같은 User의 UserStatus가 이미 존재하는지 확인
        if (userStatusRepository.existsByUserId(request.userId())) {
            throw new IllegalArgumentException("UserStatus already exists for user: " + request.userId());
        }

        UserStatus userStatus = new UserStatus(request.userId(), request.lastAccessAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + id));
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for user: " + userId));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + id));
        userStatus.update(request.newLastActiveAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for user: " + userId));
        userStatus.update(request.newLastActiveAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }
}