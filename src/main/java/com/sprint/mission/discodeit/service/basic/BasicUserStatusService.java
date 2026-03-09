package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusDto create(UUID userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 User가 존재하지 않습니다."));

        if (userStatusRepository.findByUser_Id(userId).isPresent()) {
            throw new IllegalArgumentException("이미 해당 User의 UserStatus가 존재합니다.");
        }

        UserStatus status = new UserStatus(user);
        userStatusRepository.save(status);

        return userStatusMapper.toDto(status);
    }

    @Override
    public UserStatusDto findById(UUID id) {

        UserStatus entity = userStatusRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("UserStatus를 찾을 수 없습니다: " + id));

        return userStatusMapper.toDto(entity);
    }

    @Override
    public List<UserStatusDto> findAll() {

        return userStatusRepository.findAll().stream()
            .map(userStatusMapper::toDto)
            .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {

        userStatusRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserStatusDto updateStatus(UUID userId, UserStatusUpdateRequest request) {

        UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
            .orElseThrow(() -> new IllegalArgumentException("UserStatus not found"));

        userStatus.setLastActiveAt(request.getNewLastActiveAt());

        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }
}