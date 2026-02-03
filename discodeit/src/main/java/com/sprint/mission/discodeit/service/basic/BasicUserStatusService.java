package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.DTO.UserStatusCreateRequest;
import com.sprint.mission.discodeit.service.DTO.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.status.UserStatusInterface;
import com.sprint.mission.discodeit.status.adds.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusInterface userStatusInterface;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        if(!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("없는 유저");
        }
        if(userStatusInterface.findByUser(request.userId()).isPresent()){
            throw new IllegalStateException("이미 읽은 상태입니다.");
        }
        UserStatus userStatus = new UserStatus(
                UUID.randomUUID(),
                request.userId(),
                Instant.now(),
                Instant.now(),
                Instant.now()
        );

        userStatusInterface.save(userStatus);
        return userStatus;
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusInterface.findById(id)
                .orElseThrow(() -> new NoSuchElementException("유저 없음"));
    }


    @Override
    public List<UserStatus> findAll() {
        return userStatusInterface.findAll();
    }

    @Override
    public UserStatus update(UserStatusUpdateByUserIdRequest request) {
        UserStatus userStatus = userStatusInterface.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("유저 없음"));
        userStatus.updateLastView(request.lastViewAt());
        userStatusInterface.save(userStatus);

        return userStatus;
    }

    @Override
    public UserStatus updateByUserId(UserStatusUpdateByUserIdRequest request) {
        UserStatus userStatus = userStatusInterface.findByUser(request.userId())
                .orElseThrow(()-> new NoSuchElementException("유저 없음"));

        userStatus.updateLastView(request.lastViewAt());
        userStatusInterface.save(userStatus);
        return userStatus;
    }

    @Override
    public void delete(UUID id) {
        userStatusInterface.deleteById(id);
    }
}
