package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusDTO create(UserStatusCreateRequest request) {
        if (userRepository.find(request.getUserId()) == null) {
            throw new IllegalArgumentException("User not found.");
        }
        if (userStatusRepository.findByUserId(request.getUserId()) != null) {
            throw new IllegalArgumentException("UserStatus already exists.");
        }

        UserStatus status = new UserStatus(request.getUserId());
        userStatusRepository.save(status);
        return toDto(status);
    }

    @Override
    public UserStatusDTO find(UUID id) {
        UserStatus status = userStatusRepository.find(id);
        if (status == null) {
            return null;
        }
        return toDto(status);
    }

    @Override
    public List<UserStatusDTO> findAll() {
        List<UserStatusDTO> result = new ArrayList<>();
        for (UserStatus status : userStatusRepository.findAll()) {
            result.add(toDto(status));
        }
        return result;
    }

    @Override
    public UserStatusDTO update(UserStatusUpdateRequest request) {
        UserStatus status = userStatusRepository.find(request.getId());
        if (status == null) {
            throw new IllegalArgumentException("UserStatus not found.");
        }
        status.updateActiveTime(request.getLastActiveAt());
        return toDto(status);
    }

    @Override
    public UserStatusDTO updateByUserId(UUID userId) {
        UserStatus status = userStatusRepository.findByUserId(userId);
        if (status == null) {
            throw new IllegalArgumentException("UserStatus not found.");
        }
        status.updateActiveTime(null);
        return toDto(status);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.delete(id);
    }

    private UserStatusDTO toDto(UserStatus status) {
        UserStatusDTO dto = new UserStatusDTO();
        dto.setId(status.getId());
        dto.setUserId(status.getUserId());
        dto.setLastActiveAt(status.getLastActiveAt());
        dto.setOnline(status.isOnline());
        return dto;
    }
}
