package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UUID userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 User가 존재하지 않습니다."));

        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("이미 해당 User의 UserStatus가 존재합니다.");
        }

        UserStatus status = new UserStatus(userId);
        userStatusRepository.save(status);

        return UserStatusResponse.from(status);
    }

    @Override
    public UserStatusResponse findById(UUID id) {
        return userStatusRepository.findById(id)
                .map(UserStatusResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(UserStatusResponse::from)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.delete(id);
    }

    public UserStatus updateStatus(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("UserStatus with userId " + userId + " not found"));

        userStatus.setLastActiveAt(request.getNewLastActiveAt());
        userStatusRepository.save(userStatus);

        return userStatus;
    }
}

