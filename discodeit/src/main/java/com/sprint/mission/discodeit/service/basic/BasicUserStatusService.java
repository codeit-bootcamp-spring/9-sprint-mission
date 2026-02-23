package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
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

    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        UUID userId = request.userId();
        if (userRepository.findByID(userId).isEmpty()){
            throw new NoSuchElementException("create ReadStatus 오류 | 유저가 존재하지 않음: " + userId);
        }


        UUID statusId = userRepository.findByID(userId).orElseThrow().getUserStateId();
        if (userStatusRepository.findByID(statusId).isEmpty()){
            throw new NoSuchElementException("create ReadStatus 오류 | 이미 해당 유저에 대한 ReadStatus 존재함: " + userId);
        }

        UserStatus userStatus = new UserStatus(
                userId
        );
        userStatusRepository.save(userStatus);
        return userStatus;
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findByID(id).orElseThrow();
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID id, UserStatusUpdateRequest request) {
        UserStatus target = userStatusRepository.findByID(id).orElseThrow();
        target.updateLastActiveAt(request.newLastActiveAt());
        userStatusRepository.save(target);
        return target;
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(
                () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
        userStatus.updateLastActiveAt(newLastActiveAt);
        userStatusRepository.save(userStatus);
        return userStatus;
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.remove(id);
        System.out.println("UserStatus 삭제 - ID: " + id);
    }
}
