package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Transactional
    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        UUID userId = request.userId();
        if (userRepository.findById(userId).isEmpty()){
            throw new NoSuchElementException("create ReadStatus 오류 | 유저가 존재하지 않음: " + userId);
        }


        UUID statusId = userRepository.findById(userId).orElseThrow().getStatus().getId();
        if (userStatusRepository.findById(statusId).isEmpty()){
            throw new NoSuchElementException("create ReadStatus 오류 | 이미 해당 유저에 대한 ReadStatus 존재함: " + userId);
        }

        UserStatus userStatus = new UserStatus(
            userRepository.findById(userId).orElseThrow()
        );
        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto find(UUID id) {
        return userStatusMapper.toDto(userStatusRepository.findById(id).orElseThrow());
    }

    @Override
    public List<UserStatusDto> findAll() {

        return userStatusRepository.findAll()
            .stream()
            .map(userStatusMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public UserStatusDto update(UUID id, UserStatusUpdateRequest request) {
        UserStatus target = userStatusRepository.findById(id).orElseThrow();
        target.updateLastActiveAt(request.newLastActiveAt());
        userStatusRepository.save(target);
        return userStatusMapper.toDto(target);
    }

    @Transactional
    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(
                () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
        userStatus.updateLastActiveAt(newLastActiveAt);
        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }
}
