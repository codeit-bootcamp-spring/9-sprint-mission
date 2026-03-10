package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        UUID userId = request.userId();

        // 중복되던 existsById 조회를 제거하고, findById 한 번으로 존재 여부 확인과 객체 획득을 동시에 처리합니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " does not exist"));

        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("UserStatus with id " + userId + " already exists");
        }

        UserStatus userStatus = new UserStatus(user, request.lastActiveAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
    }

    @Override
    public List<UserStatus> findAll() {
        // 불필요한 stream().toList() 변환을 제거했습니다.
        return userStatusRepository.findAll();
    }

    @Transactional
    @Override
    public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));

        // 더티 체킹을 통해 트랜잭션 종료 시 자동 반영됩니다.
        userStatus.updateLastActiveAt(request.newLastActiveAt());
        return userStatus;
    }

    @Transactional
    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseGet(() -> userStatusRepository.save(new UserStatus(user, request.newLastActiveAt())));

        userStatus.updateLastActiveAt(request.newLastActiveAt());
        return userStatus;
    }

    @Transactional
    @Override
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
        }
        userStatusRepository.deleteById(userStatusId);
    }
}