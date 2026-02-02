package com.sprint.mission.discodeit.service.Basic;

import com.sprint.mission.discodeit.DTO.UserStatusService.Request.CreateUserStatusRequest;
import com.sprint.mission.discodeit.DTO.UserStatusService.Request.UpdateUserStatusRequest;
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
        if (userRepository.findByID(userId) == null){
            throw new NoSuchElementException("create ReadStatus 오류 | 유저가 존재하지 않음: " + userId);
        }

        if (userStatusRepository.findByUserID(userId) == null){
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
        return userStatusRepository.findByID(id);
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UpdateUserStatusRequest request) {
        UserStatus target = this.find(request.userId());

        return target;
    }

    @Override
    public UserStatus updateByUserId(UUID id, UpdateUserStatusRequest request) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
