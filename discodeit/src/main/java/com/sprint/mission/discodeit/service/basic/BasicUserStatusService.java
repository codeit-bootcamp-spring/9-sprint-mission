package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userStatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
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
    public UserStatus create(CreateUserStatusRequest request) {
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
    public UserStatus update(UpdateUserStatusRequest request) {
        UserStatus target = userStatusRepository.findByID(request.id()).orElseThrow();
        target.updateLastActiveAt(request.lastActiveAt());
        userStatusRepository.save(target);
        return target;
    }

    @Override
    public UserStatus updateByUserId(UpdateUserStatusRequest request) {
        UUID userId = request.userId();
        UUID statusId = userRepository.findByID(userId).orElseThrow().getUserStateId();
        UserStatus target = userStatusRepository.findByID(statusId).orElseThrow();
        target.updateLastActiveAt(request.lastActiveAt());
        userStatusRepository.save(target);
        return target;
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.remove(id);
        System.out.println("UserStatus 삭제 - ID: " + id);
    }
}
